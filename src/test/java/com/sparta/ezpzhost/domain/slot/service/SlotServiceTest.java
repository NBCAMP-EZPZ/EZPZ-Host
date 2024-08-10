package com.sparta.ezpzhost.domain.slot.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.dto.SignupRequestDto;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzhost.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzhost.domain.slot.dto.*;
import com.sparta.ezpzhost.domain.slot.entity.Slot;
import com.sparta.ezpzhost.domain.slot.repository.SlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    SlotRepository slotRepository;

    @Mock
    PopupRepository popupRepository;

    @Mock
    ReservationRepository reservationRepository;

    @InjectMocks
    SlotService slotService;

    private Host host;
    private Popup approvePopup;
    private Popup rejectedPopup;
    private Slot slot;
    private SlotRequestDto slotRequestDto;

    @BeforeEach
    void setUp() {
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "testId123123",
                "testPassword!12",
                "testEmail@email.com",
                "testCompanyName",
                "1234567890"
        );
        host = Host.of(signupRequestDto, "testPassword!12");
        setId(host, 1L);

        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile image1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", "test image content".getBytes());

        PopupRequestDto popupRequestDto = new PopupRequestDto(
                "testTitle",
                "testContents",
                thumbnail,
                "서울특별시 강남구",
                "testManagerName",
                "01012345678",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                List.of(image1, image2)
        );

        ImageResponseDto imageResponseDto = new ImageResponseDto("testUrl", "testName");

        approvePopup = Popup.of(host, popupRequestDto, imageResponseDto, PopupStatus.SCHEDULED, ApprovalStatus.APPROVED);
        rejectedPopup = Popup.of(host, popupRequestDto, imageResponseDto, PopupStatus.SCHEDULED, ApprovalStatus.REJECTED);
        setId(approvePopup, 1L);
        setId(rejectedPopup, 1L);

        slotRequestDto = new SlotRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                1,
                10
        );

        slot = Slot.of(SlotCreateDto.of(LocalDate.now(), LocalTime.of(11, 0), 1, 10, approvePopup));
        setId(slot, 1L);
    }

    @Test
    @DisplayName("예약 가능한 슬롯 생성")
    void 예약가능한슬롯생성() {
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.existsByPopupId(anyLong())).thenReturn(false);

        List<SlotResponseDto> result = slotService.createSlot(approvePopup.getId(), slotRequestDto, host);

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getAvailableCount()).isEqualTo(1);
        verify(slotRepository, times(1)).saveAll(anyList());
        verify(popupRepository, times(1)).findByIdAndHostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("예약 가능한 슬롯 생성 - 이미 생성된 슬롯이 있는 경우")
    void 예약가능한슬롯생성_실패_이미생성된슬롯이있음() {
        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.existsByPopupId(anyLong())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> slotService.createSlot(approvePopup.getId(), slotRequestDto, host));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.SLOT_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("예약 가능한 슬롯 생성 - 팝업이 없는 경우")
    void 예약가능한슬롯생성_실패_팝업이없는경우() {
        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> slotService.createSlot(approvePopup.getId(), slotRequestDto, host));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.POPUP_ACCESS_FORBIDDEN);
    }

    @Test
    @DisplayName("예약 가능한 슬롯 생성 - 팝업이 승인되지 않은 경우")
    void 예약가능한슬롯생성_실패_팝업이승인되지않은경우() {
        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(rejectedPopup));

        CustomException exception = assertThrows(CustomException.class, () -> slotService.createSlot(approvePopup.getId(), slotRequestDto, host));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.POPUP_NOT_APPROVAL);
    }

    @Test
    @DisplayName("예약 가능한 슬롯 생성 - 예약 가능한 날짜가 아닌 경우")
    void 예약가능한슬롯생성_실패_예약가능한날짜가아닌경우() {
        //given
        slotRequestDto = new SlotRequestDto(
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                1,
                10
        );

        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.existsByPopupId(anyLong())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> slotService.createSlot(approvePopup.getId(), slotRequestDto, host));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_DATE_TIME);
    }


    @Test
    @DisplayName("예약 정보 슬롯 전체 조회")
    void 예약정보슬롯전체조회() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Slot> slotPage = new PageImpl<>(List.of(slot));

        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.findByPopupId(anyLong(), any())).thenReturn(slotPage);

        Page<SlotResponseListDto> slots = slotService.findSlots(approvePopup.getId(), pageable, host);

        //then
        assertThat(slots).isNotNull();
        assertThat(slots.getContent().size()).isEqualTo(1);
        assertThat(slots.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예약 정보 슬롯 전체 조회 - 팝업이 없는 경우")
    void 예약정보슬롯전체조회_팝업이없는경우() {
        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> slotService.findSlots(approvePopup.getId(), PageRequest.of(0, 10), host));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.POPUP_ACCESS_FORBIDDEN);
    }

    @Test
    @DisplayName("예약 정보 슬롯 수정 성공")
    void 예약정보슬롯수정성공() {
        //given
        SlotUpdateDto slotUpdateDto = new SlotUpdateDto(1, 10, "FINISHED");

        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.findByIdAndPopupId(anyLong(), anyLong())).thenReturn(Optional.of(slot));

        SlotResponseDto slotResponseDto = slotService.updateSlot(approvePopup.getId(), slot.getId(), slotUpdateDto, host);

        //then
        assertThat(slotResponseDto).isNotNull();
        assertThat(slotResponseDto.getAvailableCount()).isEqualTo(1);
        assertThat(slotResponseDto.getTotalCount()).isEqualTo(10);
        assertThat(slotResponseDto.getSlotStatus()).isEqualTo("FINISHED");
    }

    @Test
    @DisplayName("예약 정보 슬롯 수정 - 슬롯이 없는 경우")
    void 예약정보슬롯수정_실패_슬롯이없는경우() {
        //give
        SlotUpdateDto slotUpdateDto = new SlotUpdateDto(1, 10, "FINISHED");

        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.findByIdAndPopupId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> slotService.updateSlot(approvePopup.getId(), 1L, slotUpdateDto, host));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.SLOT_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 정보 슬롯 삭제")
    void 예약정보슬룻삭제() {
        //when
        when(popupRepository.findByIdAndHostId(anyLong(), anyLong())).thenReturn(Optional.of(approvePopup));
        when(slotRepository.findByIdAndPopupId(anyLong(), anyLong())).thenReturn(Optional.of(slot));

        slotService.deleteSlot(approvePopup.getId(), slot.getId(), host);

        //then
        verify(slotRepository, times(1)).delete(slot);
    }


    // Reflection을 사용하여 ID 설정하는 메소드
    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
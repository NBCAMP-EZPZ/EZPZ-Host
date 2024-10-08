package com.sparta.ezpzhost.domain.slot.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.lock.DistributedLock;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzhost.domain.reservation.dto.ReservationListDto;
import com.sparta.ezpzhost.domain.reservation.entity.Reservation;
import com.sparta.ezpzhost.domain.reservation.enums.ReservationStatus;
import com.sparta.ezpzhost.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzhost.domain.slot.dto.*;
import com.sparta.ezpzhost.domain.slot.entity.Slot;
import com.sparta.ezpzhost.domain.slot.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.ezpzhost.common.exception.ErrorType.*;
import static com.sparta.ezpzhost.common.util.PageUtil.validatePageableWithPage;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final PopupRepository popupRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 정보 슬롯 생성
     *
     * @param popupId    팝업 ID
     * @param requestDto 슬롯 생성 요청 DTO
     * @param host       로그인 사용자 정보
     * @return 생성된 슬롯 리스트
     */
    @DistributedLock(key = "'createSlot-popupId-'.concat(#popupId)")
    public List<SlotResponseDto> createSlot(Long popupId, SlotRequestDto requestDto, Host host) {
        Popup popup = getApprovedPopup(popupId, host.getId());
        validateDuplicateSlot(popupId);

        LocalDate startDate = requestDto.getStartDate();
        LocalDate endDate = requestDto.getEndDate();
        LocalTime startTime = requestDto.getStartTime();
        LocalTime endTime = requestDto.getEndTime();
        int availableCount = requestDto.getAvailableCount();
        int totalCount = requestDto.getTotalCount();

        validateDateTime(requestDto, popup);

        List<Slot> slotList = new ArrayList<>();

        // 예약 가능한 슬롯 생성
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (LocalTime time = startTime; !time.isAfter(endTime); time = time.plusHours(1)) {
                Slot slot = Slot.of(SlotCreateDto.of(date, time, availableCount, totalCount, popup));
                slotList.add(slot);
            }
        }

        slotRepository.saveAll(slotList);

        return SlotResponseDto.listOf(slotList);
    }

    // 분산락 미적용 테스트 서비스
    @Transactional
    public List<SlotResponseDto> createSlotWithoutLock(Long popupId, SlotRequestDto requestDto, Host host) {
        Popup popup = getApprovedPopup(popupId, host.getId());
        validateDuplicateSlot(popupId);

        LocalDate startDate = requestDto.getStartDate();
        LocalDate endDate = requestDto.getEndDate();
        LocalTime startTime = requestDto.getStartTime();
        LocalTime endTime = requestDto.getEndTime();
        int availableCount = requestDto.getAvailableCount();
        int totalCount = requestDto.getTotalCount();

        validateDateTime(requestDto, popup);

        List<Slot> slotList = new ArrayList<>();

        // 예약 가능한 슬롯 생성
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (LocalTime time = startTime; !time.isAfter(endTime); time = time.plusHours(1)) {
                Slot slot = Slot.of(SlotCreateDto.of(date, time, availableCount, totalCount, popup));
                slotList.add(slot);
            }
        }

        slotRepository.saveAll(slotList);

        return SlotResponseDto.listOf(slotList);
    }

    /**
     * 예약 정보 슬롯 전체 조회
     *
     * @param popupId  팝업 ID
     * @param pageable 페이지 정보
     * @param host     로그인 사용자 정보
     * @return 슬롯 리스트
     */
    @Transactional(readOnly = true)
    public Page<SlotResponseListDto> findSlots(Long popupId, Pageable pageable, Host host) {
        getApprovedPopup(popupId, host.getId());
        Page<Slot> slotList = slotRepository.findByPopupId(popupId, pageable);
        validatePageableWithPage(pageable, slotList);

        return slotList.map(SlotResponseListDto::of);
    }

    /**
     * 예약 정보 상세 조회
     *
     * @param popupId 팝업 ID
     * @param slotId  슬롯 ID
     * @param host    로그인 사용자 정보
     * @return 예약 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<ReservationListDto> findSlot(Long popupId, Long slotId, Host host) {
        getApprovedPopup(popupId, host.getId());

        List<Reservation> reservationList = reservationRepository.findBySlotIdAndReservationStatus(slotId, ReservationStatus.READY);

        if (reservationList.isEmpty()) {
            throw new CustomException(RESERVATION_NOT_FOUND);
        }

        return ReservationListDto.listOf(reservationList);
    }

    /**
     * 예약 정보 슬롯 수정
     *
     * @param popupId    팝업 ID
     * @param slotId     슬롯 ID
     * @param requestDto 슬롯 수정 요청 DTO
     * @param host       로그인 사용자 정보
     * @return 수정된 슬롯 정보
     */
    @Transactional
    public SlotResponseDto updateSlot(Long popupId, Long slotId, SlotUpdateDto requestDto, Host host) {
        getApprovedPopup(popupId, host.getId());
        Slot slot = getSlot(popupId, slotId);

        slot.update(requestDto);

        return SlotResponseDto.of(slot);
    }

    /**
     * 예약 정보 슬롯 삭제
     *
     * @param popupId 팝업 ID
     * @param slotId  슬롯 ID
     * @param host    로그인 사용자 정보
     */
    @Transactional
    public void deleteSlot(Long popupId, Long slotId, Host host) {
        getApprovedPopup(popupId, host.getId());
        Slot slot = getSlot(popupId, slotId);

        slotRepository.delete(slot);
    }



    /* UTIL */


    /**
     * 팝업 조회 및 권한 확인
     *
     * @param popupId 팝업 ID
     * @param hostId  호스트 ID
     * @return 팝업
     */
    private Popup getApprovedPopup(Long popupId, Long hostId) {
        Popup popup = popupRepository.findByIdAndHostId(popupId, hostId)
                .orElseThrow(() -> new CustomException(POPUP_ACCESS_FORBIDDEN));

        if (!popup.getApprovalStatus().equals(ApprovalStatus.APPROVED)) {
            throw new CustomException(POPUP_NOT_APPROVAL);
        }

        return popup;
    }

    /**
     * 슬롯이 이미 생성된 팝업인지 확인
     *
     * @param popupId 팝업 ID
     */
    private void validateDuplicateSlot(Long popupId) {
        if (slotRepository.existsByPopupId(popupId)) {
            throw new CustomException(SLOT_ALREADY_EXISTS);
        }
    }

    /**
     * 예약 가능한 날짜, 시간 확인
     *
     * @param dto   슬롯 생성 요청 DTO
     * @param popup 팝업
     */
    private void validateDateTime(SlotRequestDto dto, Popup popup) {
        if (dto.getStartDate().isBefore(ChronoLocalDate.from(popup.getStartDate()))
                || dto.getEndDate().isAfter(ChronoLocalDate.from(popup.getEndDate()))
                || dto.getStartDate().isAfter(dto.getEndDate())
                || dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new CustomException(INVALID_DATE_TIME);
        }
    }

    /**
     * 슬롯 조회
     *
     * @param popupId 팝업 ID
     * @param slotId  슬롯 ID
     * @return 슬롯 정보
     */
    private Slot getSlot(Long popupId, Long slotId) {
        return slotRepository.findByIdAndPopupId(slotId, popupId)
                .orElseThrow(() -> new CustomException(SLOT_NOT_FOUND));
    }

}

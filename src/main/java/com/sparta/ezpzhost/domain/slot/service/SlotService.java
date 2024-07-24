package com.sparta.ezpzhost.domain.slot.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.slot.dto.SlotCreateDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotRequestDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotResponseDto;
import com.sparta.ezpzhost.domain.slot.entity.Slot;
import com.sparta.ezpzhost.domain.slot.repository.SlotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlotService {
	private final SlotRepository slotRepository;
	private final PopupRepository popupRepository;
	
	/**
	 * 예약 정보 슬롯 생성
	 *
	 * @param popupId 팝업 ID
	 * @param requestDto 슬롯 생성 요청 DTO
	 * @param host 로그인 사용자 정보
	 * @return 생성된 슬롯 리스트
	 */
	@Transactional
	public List<SlotResponseDto> createSlot(Long popupId, SlotRequestDto requestDto, Host host) {
		Popup popup = validatePopup(popupId, host.getId());
		existPopupSlot(popupId);
		
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
	
	
	/* UTIL */
	
	
	/**
	 * 팝업 조회 및 권한 확인
	 *
	 * @param popupId 팝업 ID
	 * @param hostId 호스트 ID
	 * @return 팝업
	 */
	private Popup validatePopup(Long popupId, Long hostId) {
		Popup popup = popupRepository.findByIdAndHostId(popupId, hostId)
			.orElseThrow(() -> new CustomException(ErrorType.POPUP_ACCESS_FORBIDDEN));
		
		if (popup.getApprovalStatus().equals(ApprovalStatus.APPROVED)) {
			throw new CustomException(ErrorType.POPUP_NOT_APPROVAL);
		}
		
		return popup;
	}
	
	/**
	 * 슬롯이 이미 생성된 팝업인지 확인
	 *
	 * @param popupId 팝업 ID
	 */
	private void existPopupSlot(Long popupId) {
		if (slotRepository.existsByPopupId(popupId)) {
			// 이미 슬롯이 생성된 팝업인 경우
			throw new CustomException(ErrorType.SLOT_ALREADY_EXISTS);
		}
	}
	
	/**
	 * 예약 가능한 날짜, 시간 확인
	 *
	 * @param requestDto 슬롯 생성 요청 DTO
	 * @param popup 팝업
	 */
	private void validateDateTime(SlotRequestDto requestDto, Popup popup) {
		if (requestDto.getStartDate().isBefore(ChronoLocalDate.from(popup.getStartDate()))
			|| requestDto.getEndDate().isAfter(ChronoLocalDate.from(popup.getEndDate()))
			|| requestDto.getStartDate().isAfter(requestDto.getEndDate())
			|| requestDto.getStartTime().isAfter(requestDto.getEndTime())) {
			throw new CustomException(ErrorType.INVALID_DATE_TIME);
		}
	}
}

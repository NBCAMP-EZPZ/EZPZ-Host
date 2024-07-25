package com.sparta.ezpzhost.domain.slot.controller;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.domain.reservation.dto.ReservationListDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotRequestDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotResponseDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotResponseListDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotUpdateDto;
import com.sparta.ezpzhost.domain.slot.service.SlotService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/popups/{popupId}/slots")
public class SlotController {
	private final SlotService slotService;

	/**
	 * 예약 정보 슬롯 등록
	 *
	 * @param popupId 팝업 ID
	 * @param requestDto 슬롯 생성 요청 DTO
	 * @param userDetails 로그인 사용자 정보
	 * @return 생성된 슬롯 리스트
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<?>> createSlot(
		@PathVariable Long popupId,
		@Valid @RequestBody SlotRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		List<SlotResponseDto> slotList = slotService.createSlot(popupId, requestDto, userDetails.getHost());

		return getResponseEntity(slotList, "예약 정보 등록 성공");
	}
	
	/**
	 * 예약 정보 슬롯 전체 조회
	 *
	 * @param popupId 팝업 ID
	 * @param pageable 페이지 정보
	 * @param userDetails 로그인 사용자 정보
	 * @return 슬롯 리스트
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<?>> findSlots(
		@PathVariable Long popupId,
		Pageable pageable,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		Page<SlotResponseListDto> slotList = slotService.findSlots(popupId, pageable, userDetails.getHost());

		return getResponseEntity(slotList, "예약 정보 조회 성공");
	}
	
	/**
	 * 예약 정보 상세 조회
	 *
	 * @param popupId
	 * @param slotId
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/{slotId}")
	public ResponseEntity<CommonResponse<?>> findSlot(
		@PathVariable Long popupId,
		@PathVariable Long slotId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		List<ReservationListDto> reservationList = slotService.findSlot(popupId, slotId, userDetails.getHost());

		return getResponseEntity(reservationList, "예약 정보 상세 조회 성공");
	}
	
	/**
	 * 예약 정보 수정
	 *
	 * @param popupId 팝업 ID
	 * @param slotId 슬롯 ID
	 * @param requestDto 슬롯 수정 요청 DTO
	 * @param userDetails 로그인 사용자 정보
	 * @return 수정된 슬롯 정보
	 */
	@PatchMapping("/{slotId}")
	public ResponseEntity<CommonResponse<?>> updateSlot(
		@PathVariable Long popupId,
		@PathVariable Long slotId,
		@Valid @RequestBody SlotUpdateDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		SlotResponseDto slotResponseDto =  slotService.updateSlot(popupId, slotId, requestDto, userDetails.getHost());

		return getResponseEntity(slotResponseDto, "예약 정보 수정 성공");
	}
	
	/**
	 * 예약 정보 삭제
	 *
	 * @param popupId 팝업 ID
	 * @param slotId 슬롯 ID
	 * @param userDetails 로그인 사용자 정보
	 * @return 삭제된 슬롯 정보
	 */
	@DeleteMapping("/{slotId}")
	public ResponseEntity<CommonResponse<?>> deleteSlot(
		@PathVariable Long popupId,
		@PathVariable Long slotId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		slotService.deleteSlot(popupId, slotId, userDetails.getHost());

		return getResponseEntity(null, "예약 정보 삭제 성공");
	}
}

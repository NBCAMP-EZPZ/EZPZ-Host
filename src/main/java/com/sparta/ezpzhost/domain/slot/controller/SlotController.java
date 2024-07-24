package com.sparta.ezpzhost.domain.slot.controller;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.domain.slot.dto.SlotRequestDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotResponseDto;
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
}

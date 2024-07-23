package com.sparta.ezpzhost.domain.popup.controller;

import com.sparta.ezpzhost.common.dto.PageDto;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzhost.domain.popup.service.PopupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    /**
     * 팝업 등록
     * @param requestDto 팝업 등록 정보
     * @return 팝업 정보
     */
    @PostMapping("/v1/popups")
    public ResponseEntity<?> createPopup(
            @ModelAttribute @Valid PopupRequestDto requestDto) {
        // todo : securiry 구현 완료 시 변경
        Host host = new Host(1L);
        PopupResponseDto responseDto = popupService.createPopup(requestDto, host);
        return getResponseEntity(responseDto, "팝업스토어 등록 성공");
    }

    /**
     * 상태별 팝업 목록 조회
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param approvalStatusBy 승인 상태
     * @param popupStatus 팝업 상태
     * @return 팝업 목록
     */
    @GetMapping("/v1/popups")
    public ResponseEntity<?> findAllPopupsByStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "all") String approvalStatusBy,
            @RequestParam(defaultValue = "all") String popupStatus) {
        // todo : securiry 구현 완료 시 변경
        Host host = new Host(1L);

        PageDto pageDto = new PageDto(page, size, sortBy, approvalStatusBy, popupStatus);

        Page<?> popupList = popupService.findAllPopupsByStatus(host, pageDto);
        return getResponseEntity(popupList, "호스트의 팝업 목록 조회 성공");
    }

    /**
     * 팝업 상세 조회
     * @param popupId 팝업 ID
     * @return 팝업 상세정보
     */
    @GetMapping("/v1/popups/{popupId}")
    public ResponseEntity<?> findPopup(@PathVariable Long popupId) {
        // todo : securiry 구현 완료 시 변경
        Host host = new Host(1L);
        PopupResponseDto responseDto = popupService.findPopup(popupId, host);
        return getResponseEntity(responseDto, "핍업스토어 상세보기 조회 성공");
    }
}

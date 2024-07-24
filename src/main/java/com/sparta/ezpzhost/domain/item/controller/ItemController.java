package com.sparta.ezpzhost.domain.item.controller;

import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.domain.item.dto.ItemCondition;
import com.sparta.ezpzhost.domain.item.dto.ItemRequestDto;
import com.sparta.ezpzhost.domain.item.dto.ItemResponseDto;
import com.sparta.ezpzhost.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록
     * @param popupId 팝업 ID
     * @param requestDto 상품 등록 정보
     * @param userDetails 호스트
     * @return 상품 정보
     */
    @PostMapping("/v1/popups/{popupId}/items")
    public ResponseEntity<?> createItem(
            @PathVariable Long popupId,
            @ModelAttribute @Valid ItemRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemResponseDto responseDto = itemService.createItem(userDetails.getHost(), popupId, requestDto);
        return getResponseEntity(responseDto, "굿즈 등록 성공");
    }

    /**
     * 팝업 및 상태별 상품 목록 조회
     * @param pageable 페이징
     * @param popupId 팝업 ID
     * @param itemStatus 상품 상태
     * @param userDetails 호스트
     * @return 상품 목록
     */
    @GetMapping("/v1/items")
    public ResponseEntity<?> findAllItemsByPopupAndStatus(
            Pageable pageable,
            @RequestParam(defaultValue = "all") String popupId,
            @RequestParam(defaultValue = "all") String itemStatus,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemCondition cond = ItemCondition.of(popupId, itemStatus);

        Page<?> itemList = itemService.findAllItemsByPopupAndStatus(userDetails.getHost(), pageable, cond);
        PageUtil.validatePageableWithPage(pageable, itemList);
        return getResponseEntity(itemList, "굿즈 목록 조회 성공");
    }

    /**
     * 상품 상세 조회
     * @param itemId 상품 ID
     * @param userDetails 호스트
     * @return 상품 상세정보
     */
    @GetMapping("/v1/items/{itemId}")
    public ResponseEntity<?> findItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemResponseDto responseDto = itemService.findItem(userDetails.getHost(), itemId);
        return getResponseEntity(responseDto, "굿즈 상세보기 조회 성공");
    }

    /**
     * 상품 수정
     * @param itemId 상품 ID
     * @param requestDto 상품 수정 정보
     * @param userDetails 호스트
     * @return 상품 정보
     */
    @PutMapping("/v1/items/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long itemId,
            @ModelAttribute @Valid ItemRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemResponseDto responseDto = itemService.updateItem(itemId, requestDto, userDetails.getHost());
        return getResponseEntity(responseDto, "상품 수정 성공");
    }

    /**
     * 상품 상태 변경
     * @param itemId 상품 ID
     * @param itemStatus 상품 상태
     * @param userDetails 호스트
     * @return 성공 메시지
     */
    @PatchMapping("/v1/items/{itemId}")
    public ResponseEntity<?> changeItemStatus(
            @PathVariable Long itemId,
            @RequestParam(required = false) String itemStatus,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        itemService.changeItemStatus(itemId, itemStatus, userDetails.getHost());
        return getResponseEntity("상품 상태 변경 성공");
    }
}

package com.sparta.ezpzhost.domain.item.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.dto.ItemCondition;
import com.sparta.ezpzhost.domain.item.dto.ItemPageResponseDto;
import com.sparta.ezpzhost.domain.item.dto.ItemRequestDto;
import com.sparta.ezpzhost.domain.item.dto.ItemResponseDto;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.service.ImageService;
import com.sparta.ezpzhost.domain.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final PopupService popupService;
    private final ImageService imageService;

    /**
     * 상품 등록
     *
     * @param host       호스트
     * @param popupId    팝업 ID
     * @param requestDto 상품 등록 정보
     * @return 상품 정보
     */
    @Transactional
    public ItemResponseDto createItem(Host host, Long popupId, ItemRequestDto requestDto) {

        // 굿즈명 중복 체크
        duplicatedItemName(requestDto.getName());

        Popup popup = popupService.findPopupByIdAndHostId(popupId, host.getId());
        popup.checkItemCanBeRegistered();

        // 상품 이미지 업로드
        ImageResponseDto image = imageService.uploadItemImage(requestDto.getImage());

        Item item = Item.of(popup, host, requestDto, image);
        Item savedItem = itemRepository.save(item);

        return ItemResponseDto.of(savedItem);
    }

    /**
     * 팝업 및 상태별 상품 목록 조회
     *
     * @param host     호스트
     * @param pageable 페이징
     * @param cond     조회 조건
     * @return 상품 목록
     */
    public Page<?> findAllItemsByPopupAndStatus(Host host, Pageable pageable, ItemCondition cond) {
        return itemRepository.findAllItemsByPopupAndStatus(host, pageable, cond)
                .map(ItemPageResponseDto::of);
    }

    /**
     * 상품 상세 조회
     *
     * @param host   호스트
     * @param itemId 상품 ID
     * @return 상품 상세정보
     */
    public ItemResponseDto findItem(Host host, Long itemId) {
        return ItemResponseDto.of(findItemByIdAndHost(itemId, host));
    }

    /**
     * 상품 수정
     *
     * @param itemId     상품 ID
     * @param requestDto 상품 수정 정보
     * @param host       호스트
     * @return 상품 정보
     */
    @Transactional
    public ItemResponseDto updateItem(Long itemId, ItemRequestDto requestDto, Host host) {

        // 수정 권한 및 가능 여부 확인
        Item item = findItemByIdAndHost(itemId, host);
        item.checkPossibleUpdateStatus();

        // 원래 상품 이름과 요청된 상품 이름이 다른 경우에만 중복 검사 수행
        if (!item.getName().equals(requestDto.getName())) {
            duplicatedItemName(requestDto.getName());
        }

        String imageName = item.getImageName();

        // 상품 사진 변경 확인
        if (imageName.equals(requestDto.getImage().getOriginalFilename())) {
            item.update(requestDto);
        } else {
            // 상품 사진 업로드
            ImageResponseDto image = imageService.uploadItemImage(requestDto.getImage());

            item.update(requestDto, image);

            imageService.deleteItemImage(imageName);
        }

        Item savedItem = itemRepository.save(item);
        return ItemResponseDto.of(savedItem);
    }

    /**
     * 상품 상태 변경
     *
     * @param itemId     상품 ID
     * @param itemStatus 상품 상태
     * @param host       호스트
     */
    @Transactional
    public void changeItemStatus(Long itemId, String itemStatus, Host host) {
        Item item = findItemByIdAndHost(itemId, host);
        item.changeItemStatus(itemStatus);

        imageService.deleteItemImage(item.getImageName());
    }

    
    /* UTIL */

    /**
     * 상품 찾기
     *
     * @param itemId 상품 ID
     * @param host   호스트
     * @return 상품
     */
    private Item findItemByIdAndHost(Long itemId, Host host) {
        return itemRepository.findByIdAndHost(itemId, host)
                .orElseThrow(() -> new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN));
    }

    /**
     * 상품명 중복 체크
     *
     * @param itemName 상품명
     */
    private void duplicatedItemName(String itemName) {
        if (itemRepository.existsByName(itemName)) {
            throw new CustomException(ErrorType.DUPLICATED_ITEM_NAME);
        }
    }
}

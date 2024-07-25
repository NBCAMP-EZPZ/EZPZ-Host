package com.sparta.ezpzhost.domain.item.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.item.dto.ItemRequestDto;
import com.sparta.ezpzhost.domain.item.enums.ItemStatus;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "item_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    private Item(Popup popup, ItemRequestDto requestDto, ImageResponseDto image, ItemStatus itemStatus) {
        this.popup = popup;
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.price = requestDto.getPrice();
        this.stock = requestDto.getStock();
        this.imageUrl = image.getUrl();
        this.imageName = image.getName();
        this.likeCount = 0;
        this.itemStatus = itemStatus;
    }

    public static Item of(Popup popup, ItemRequestDto requestDto, ImageResponseDto image) {
        return new Item(popup, requestDto, image, ItemStatus.BEFORE_SALE);
    }

    /**
     * 수정 가능 여부 확인
     */
    public void checkPossibleUpdateStatus() {
        if (this.itemStatus.equals(ItemStatus.SALE_END) || this.itemStatus.equals(ItemStatus.SOLD_OUT)) {
            throw new CustomException(ErrorType.ITEM_ALREADY_QUIT);
        }
    }

    /**
     * 상품 수정 (사진 포함 O)
     * @param requestDto 상품 수정 정보
     * @param image 상품 사진 정보
     */
    public void update(ItemRequestDto requestDto, ImageResponseDto image) {
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.price = requestDto.getPrice();
        this.stock = requestDto.getStock();
        this.imageUrl = image.getUrl();
        this.imageName = image.getName();
    }

    /**
     * 상품 수정 (사진 포함 X)
     * @param requestDto 상품 수정 정보
     */
    public void update(ItemRequestDto requestDto) {
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.price = requestDto.getPrice();
        this.stock = requestDto.getStock();
    }

    /**
     * 상품 상태 변경
     */
    public void changeItemStatus(String itemStatus) {
        if (!StringUtils.hasText(itemStatus)) {
            throw new CustomException(ErrorType.INVALID_ITEM_STATUS);
        } else if (this.itemStatus.equals(ItemStatus.SALE_END)) {
            throw new CustomException(ErrorType.ITEM_ALREADY_QUIT);
        } else if (ItemStatus.BEFORE_SALE.equals(ItemStatus.valueOf(itemStatus.toUpperCase()))) {
            throw new CustomException(ErrorType.INVALID_ITEM_STATUS);
        }

        try {
            this.itemStatus = ItemStatus.valueOf(itemStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorType.INVALID_ITEM_STATUS);
        }
    }
}

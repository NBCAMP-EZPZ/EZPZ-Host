package com.sparta.ezpzhost.domain.item.dto;

import com.sparta.ezpzhost.domain.item.entity.Item;
import lombok.Getter;

@Getter
public class ItemPageResponseDto {

    private final Long ItemId;
    private final String name;
    private final int price;
    private final int likeCount;
    private final String image;

    public ItemPageResponseDto(Item item) {
        this.ItemId = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.likeCount = item.getLikeCount();
        this.image = item.getImageUrl();
    }

    public static ItemPageResponseDto of(Item item) {
        return new ItemPageResponseDto(item);
    }
}

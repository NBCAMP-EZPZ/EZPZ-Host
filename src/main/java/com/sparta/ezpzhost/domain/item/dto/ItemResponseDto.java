package com.sparta.ezpzhost.domain.item.dto;

import com.sparta.ezpzhost.domain.item.entity.Item;
import lombok.Getter;

@Getter
public class ItemResponseDto {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final int stock;
    private final int likeCount;
    private final String image;

    private ItemResponseDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.stock = item.getStock();
        this.likeCount = item.getLikeCount();
        this.image = item.getImageUrl();
    }

    public static ItemResponseDto of(Item item) {
        return new ItemResponseDto(item);
    }
}

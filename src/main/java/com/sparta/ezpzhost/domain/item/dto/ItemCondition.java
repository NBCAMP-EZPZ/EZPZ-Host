package com.sparta.ezpzhost.domain.item.dto;

import lombok.Getter;

@Getter
public class ItemCondition {

    private final String popupId;
    private final String ItemStatus;

    private ItemCondition(String popupId, String ItemStatus) {
        this.popupId = popupId;
        this.ItemStatus = ItemStatus;
    }

    public static ItemCondition of(String popupId, String ItemStatus) {
        return new ItemCondition(popupId, ItemStatus);
    }
}

package com.sparta.ezpzhost.domain.order.enums;

public enum OrderSearchType {
    ALL,       // 전체 조회
    BY_ITEM,    // 상품별 조회
    BY_STATUS,       // 주문 상태별 조회
    BY_ITEM_AND_STATUS;

    public static boolean isValid(String type) {
        for (OrderSearchType orderSearchType : OrderSearchType.values()) {
            if (orderSearchType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}
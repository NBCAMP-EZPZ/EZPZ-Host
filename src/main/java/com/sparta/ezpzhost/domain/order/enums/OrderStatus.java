package com.sparta.ezpzhost.domain.order.enums;

public enum OrderStatus {
    ALL,
    ORDER_COMPLETED,   // 주문 완료
    IN_TRANSIT,        // 배송 중
    DELIVERED,         // 배송 완료
    CANCELLED;         // 주문 취소

    public static boolean isValid(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}
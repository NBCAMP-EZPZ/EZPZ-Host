package com.sparta.ezpzhost.domain.order.dto;

import com.sparta.ezpzhost.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderFindAllResponseDto {

    private Long orderId;
    private int totalPrice;
    private String orderStatus;
    private String orderDate;

    private OrderFindAllResponseDto(Order order) {
        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus().toString();
        this.orderDate = order.getCreatedAt().toLocalDate().toString();
    }

    public static OrderFindAllResponseDto of(Order order) {
        return new OrderFindAllResponseDto(order);
    }
}

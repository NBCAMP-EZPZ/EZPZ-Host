package com.sparta.ezpzhost.domain.order.dto;

import com.sparta.ezpzhost.domain.order.entity.Order;
import com.sparta.ezpzhost.domain.order.enums.OrderStatus;
import com.sparta.ezpzhost.domain.orderline.dto.OrderlineResponseDto;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderResponseDto {

    private Long orderId;
    private int totalPrice;
    private OrderStatus orderStatus;
    private List<OrderlineResponseDto> orderedItems;

    private OrderResponseDto(Long orderId, int totalPrice, OrderStatus orderStatus,
            List<OrderlineResponseDto> orderedItems) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderedItems = orderedItems;
    }

    public static OrderResponseDto of(Order order, List<OrderlineResponseDto> orderedItems) {
        return new OrderResponseDto(
                order.getId(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                orderedItems
        );
    }
}

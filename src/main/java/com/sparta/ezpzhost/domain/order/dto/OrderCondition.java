package com.sparta.ezpzhost.domain.order.dto;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.order.enums.OrderSearchType;
import com.sparta.ezpzhost.domain.order.enums.OrderStatus;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class OrderCondition {

    private final OrderSearchType searchType;
    private final Long itemId;
    private final OrderStatus orderStatus;

    public OrderCondition(OrderSearchType searchType, Long itemId, OrderStatus orderStatus) {
        this.searchType = searchType;
        this.itemId = itemId != null ? itemId : -1L; // 기본값 설정
        this.orderStatus = orderStatus;
    }

    public static OrderCondition of(OrderRequestDto orderRequestDto) {
        if (!StringUtils.hasText(orderRequestDto.getSearchType()) || !OrderSearchType.isValid(
                orderRequestDto.getSearchType())) {
            throw new CustomException(ErrorType.INVALID_ORDER_SORT_CONDITION);
        }
        OrderSearchType type = OrderSearchType.valueOf(
                orderRequestDto.getSearchType().toUpperCase());
        OrderStatus status = null;

        if ((type == OrderSearchType.BY_STATUS || type == OrderSearchType.BY_ITEM_AND_STATUS) &&
                (!StringUtils.hasText(orderRequestDto.getOrderStatus()) || !OrderStatus.isValid(
                        orderRequestDto.getOrderStatus()))) {
            throw new CustomException(ErrorType.INVALID_ORDER_SORT_CONDITION);
        }

        if (type == OrderSearchType.BY_STATUS || type == OrderSearchType.BY_ITEM_AND_STATUS) {
            status = OrderStatus.valueOf(orderRequestDto.getOrderStatus().toUpperCase());
        }

        return new OrderCondition(type, orderRequestDto.getItemId(), status);
    }
}


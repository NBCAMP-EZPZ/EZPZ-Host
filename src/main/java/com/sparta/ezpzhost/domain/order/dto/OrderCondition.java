package com.sparta.ezpzhost.domain.order.dto;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.order.enums.OrderSearchType;
import com.sparta.ezpzhost.domain.order.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class OrderCondition {

    private final OrderSearchType searchType;
    private final Long itemId;
    private final OrderStatus orderStatus;

    public OrderCondition(OrderSearchType searchType, Long itemId, OrderStatus orderStatus) {
        this.searchType = searchType;
        this.itemId = itemId;
        this.orderStatus = orderStatus;
    }

    public static OrderCondition of(String searchType, Long itemId, String orderStatus) {
        if (!StringUtils.hasText(searchType) || !OrderSearchType.isValid(searchType)) {
            throw new CustomException(ErrorType.INVALID_ORDER_SORT_CONDITION);
        }
        OrderSearchType type = OrderSearchType.valueOf(searchType.toUpperCase());
        OrderStatus status = null;

        if ((type == OrderSearchType.BY_STATUS || type == OrderSearchType.BY_ITEM_AND_STATUS) &&
                (!StringUtils.hasText(orderStatus) || !OrderStatus.isValid(orderStatus))) {
            throw new CustomException(ErrorType.INVALID_ORDER_SORT_CONDITION);
        }

        if (type == OrderSearchType.BY_STATUS || type == OrderSearchType.BY_ITEM_AND_STATUS) {
            status = OrderStatus.valueOf(orderStatus.toUpperCase());
        }

        return new OrderCondition(type, itemId, status);
    }

}

package com.sparta.ezpzhost.domain.order.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> findOrdersAllByStatus(OrderCondition cond, Pageable pageable, Host host);

    Order findOrderWithDetails(Long orderId, Long hostId);
}

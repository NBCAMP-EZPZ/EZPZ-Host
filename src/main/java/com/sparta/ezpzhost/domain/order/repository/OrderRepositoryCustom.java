package com.sparta.ezpzhost.domain.order.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.entity.Order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface OrderRepositoryCustom {

    Page<Order> findOrdersAllByStatus(OrderCondition cond, Pageable pageable, Host host);
}

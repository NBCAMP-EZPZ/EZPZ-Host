package com.sparta.ezpzhost.domain.order.repository;


import com.sparta.ezpzhost.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

}

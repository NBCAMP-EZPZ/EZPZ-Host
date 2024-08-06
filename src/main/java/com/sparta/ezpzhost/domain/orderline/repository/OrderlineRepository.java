package com.sparta.ezpzhost.domain.orderline.repository;

import com.sparta.ezpzhost.domain.orderline.entity.Orderline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderlineRepository extends JpaRepository<Orderline, Long>,
        OrderlineRepositoryCustom {

}

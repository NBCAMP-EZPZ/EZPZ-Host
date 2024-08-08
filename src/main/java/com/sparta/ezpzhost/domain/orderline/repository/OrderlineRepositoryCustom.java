package com.sparta.ezpzhost.domain.orderline.repository;

import com.sparta.ezpzhost.domain.orderline.entity.Orderline;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderlineRepositoryCustom {

    List<Orderline> findRecentOrderLinesByItemId(Long itemId,
            LocalDateTime lastJobExecutionTime);

    List<Orderline> findRecentOrderLinesByPopupId(Long popupId, LocalDateTime lastJobExecutionTime);
}

package com.sparta.ezpzhost.domain.orderline.repository;

import static com.sparta.ezpzhost.domain.item.entity.QItem.item;
import static com.sparta.ezpzhost.domain.order.entity.QOrder.order;
import static com.sparta.ezpzhost.domain.orderline.entity.QOrderline.orderline;
import static com.sparta.ezpzhost.domain.popup.entity.QPopup.popup;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.order.enums.OrderStatus;
import com.sparta.ezpzhost.domain.orderline.entity.Orderline;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderlineRepositoryCustomImpl implements OrderlineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Orderline> findRecentOrderLinesByItemId(Long itemId,
            LocalDateTime lastJobExecutionTime) {
        return queryFactory.selectFrom(orderline)
                .join(orderline.order, order)
                .where(
                        orderline.item.id.eq(itemId),
                        order.orderStatus.eq(OrderStatus.ORDER_COMPLETED),
                        order.modifiedAt.after(lastJobExecutionTime)
                )
                .fetch();
    }

    @Override
    public List<Orderline> findRecentOrderLinesByPopupId(Long popupId,
            LocalDateTime lastJobExecutionTime) {
        return queryFactory.selectFrom(orderline)
                .join(orderline.order, order)
                .join(orderline.item, item)
                .join(item.popup, popup)
                .where(
                        popup.id.eq(popupId)
                                .and(order.modifiedAt.after(lastJobExecutionTime))
                                .and(order.orderStatus.eq(OrderStatus.ORDER_COMPLETED)))
                .fetch();
    }
}

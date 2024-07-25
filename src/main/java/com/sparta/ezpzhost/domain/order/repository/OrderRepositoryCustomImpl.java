package com.sparta.ezpzhost.domain.order.repository;

import static com.sparta.ezpzhost.domain.host.entity.QHost.host;
import static com.sparta.ezpzhost.domain.item.entity.QItem.item;
import static com.sparta.ezpzhost.domain.order.entity.QOrder.order;
import static com.sparta.ezpzhost.domain.orderline.entity.QOrderline.orderline;
import static com.sparta.ezpzhost.domain.popup.entity.QPopup.popup;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.entity.Order;
import com.sparta.ezpzhost.domain.order.enums.OrderSearchType;
import com.sparta.ezpzhost.domain.order.enums.OrderStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> findOrdersAllByStatus(OrderCondition cond, Pageable pageable, Host host) {
        List<Order> orders = jpaQueryFactory.selectFrom(order)
                .leftJoin(orderline).on(order.id.eq(orderline.order.id))
                .leftJoin(orderline.item, item)
                .where(
                        item.popup.host.eq(host),
                        searchTypeEq(cond)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(Wildcard.count)
                .from(order)
                .leftJoin(orderline).on(order.id.eq(orderline.order.id))
                .leftJoin(orderline.item, item)
                .where(
                        item.popup.host.eq(host),
                        searchTypeEq(cond)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(orders, pageable, () -> total);
    }

    @Override
    public Order findOrderWithDetails(Long orderId, Long hostId) {
        return jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.orderlineList, orderline).fetchJoin()  // Order와 Orderline을 조인
                .leftJoin(orderline.item, item)  // Orderline과 Item을 조인
                .leftJoin(item.popup, popup)  // Item과 Popup을 조인
                .leftJoin(popup.host, host) // Popup과 Host를 조인
                .where(order.id.eq(orderId)  // Order ID를 필터
                        .and(host.id.eq(hostId)))  // Host ID를 필터
                .fetchOne();
    }

    private BooleanExpression orderStatusEq(OrderStatus status) {
        if (status == null || status == OrderStatus.ALL) {
            return null;
        }
        return order.orderStatus.eq(status);
    }

    private BooleanExpression itemIdEq(Long itemId) {
        if (itemId == null) {
            return null;
        }
        return orderline.item.id.eq(itemId);
    }

    private BooleanExpression searchTypeEq(OrderCondition cond) {
        if (cond.getSearchType() == OrderSearchType.BY_ITEM) {
            return itemIdEq(cond.getItemId());
        } else if (cond.getSearchType() == OrderSearchType.BY_STATUS) {
            return orderStatusEq(cond.getOrderStatus());
        } else if (cond.getSearchType() == OrderSearchType.BY_ITEM_AND_STATUS) {
            return itemIdEq(cond.getItemId()).and(orderStatusEq(cond.getOrderStatus()));
        }

        return null;
    }
}
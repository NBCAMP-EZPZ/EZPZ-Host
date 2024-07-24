package com.sparta.ezpzhost.domain.item.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.dto.ItemCondition;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.enums.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzhost.domain.item.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Item> findAllItemsByPopupAndStatus(Host host, PageUtil pageUtil, ItemCondition cond) {
        JPAQuery<Item> query = findAllItemsByPopupAndStatusQuery(item, host, cond)
                .offset(pageUtil.toPageable().getOffset())
                .limit(pageUtil.toPageable().getPageSize())
                .orderBy(item.createdAt.desc());

        List<Item> items = query.fetch();
        Long totalSize = findAllItemsByPopupAndStatusCount(host, cond).fetch().get(0);

        return PageableExecutionUtils.getPage(items, pageUtil.toPageable(), () -> totalSize);
    }

    private <T> JPAQuery<T> findAllItemsByPopupAndStatusQuery(Expression<T> expr, Host host, ItemCondition cond) {
        return jpaQueryFactory.select(expr)
                .from(item)
                .where(
                        hostEq(host),
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                );
    }

    private JPAQuery<Long> findAllItemsByPopupAndStatusCount(Host host, ItemCondition cond) {
        return jpaQueryFactory.select(Wildcard.count)
                .from(item)
                .where(
                        hostEq(host),
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                );
    }

    // 조건 : 호스트
    private BooleanExpression hostEq(Host host) {
        return Objects.nonNull(host) ? item.popup.host.eq(host) : null;
    }

    // 조건 : 팝업 ID
    private BooleanExpression popupIdEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                item.popup.id.eq(Long.valueOf(statusBy)) : null;
    }

    // 조건 : 상품 상태
    private BooleanExpression itemStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                item.itemStatus.eq(ItemStatus.valueOf(statusBy.toUpperCase())) : null;
    }
}

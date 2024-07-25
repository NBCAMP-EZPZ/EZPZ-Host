package com.sparta.ezpzhost.domain.item.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.dto.ItemCondition;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.enums.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzhost.domain.item.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Item> findAllItemsByPopupAndStatus(Host host, Pageable pageable, ItemCondition cond) {
        // 데이터 조회 쿼리
        List<Item> items = jpaQueryFactory
                .select(item)
                .from(item)
                .where(
                        hostEq(host),
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdAt.desc())
                .fetch();

        // 카운트 쿼리
        Long totalSize = jpaQueryFactory
                .select(Wildcard.count)
                .from(item)
                .where(
                        hostEq(host),
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(items, pageable, () -> totalSize);
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

package com.sparta.ezpzhost.domain.salesStatistics.repository;

import static com.sparta.ezpzhost.domain.item.entity.QItem.item;
import static com.sparta.ezpzhost.domain.popup.entity.QPopup.popup;
import static com.sparta.ezpzhost.domain.salesStatistics.entity.QMonthlySalesStatistics.monthlySalesStatistics;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MonthlySalesStatisticsRepositoryCustomImpl implements
        MonthlySalesStatisticsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MonthlySalesStatistics> findAllMonthlySalesStatisticsByHost(Host host,
            Pageable pageable) {
        // 데이터 조회 쿼리
        List<MonthlySalesStatistics> statistics = jpaQueryFactory
                .select(monthlySalesStatistics)
                .from(monthlySalesStatistics)
                .join(monthlySalesStatistics.item, item)
                .leftJoin(item.popup, popup)
                .where(
                        popup.host.eq(host)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdAt.desc())
                .fetch();

        Long totalSize = jpaQueryFactory
                .select(Wildcard.count)
                .from(monthlySalesStatistics)
                .join(monthlySalesStatistics.item, item)
                .leftJoin(item.popup, popup)
                .where(
                        popup.host.eq(host)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(statistics, pageable, () -> totalSize);
    }
}

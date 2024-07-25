package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.PopupCondition;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzhost.domain.popup.entity.QPopup.popup;

@RequiredArgsConstructor
public class PopupRepositoryCustomImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Popup> findAllPopupsByStatus(Host host, Pageable pageable, PopupCondition cond) {
        // 데이터 조회 쿼리
        List<Popup> popups = jpaQueryFactory
                .select(popup)
                .from(popup)
                .where(
                        hostEq(host),
                        approvalStatusEq(cond.getApprovalStatus()),
                        popupStatusEq(cond.getPopupStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(popup.createdAt.desc())
                .fetch();

        // 카운트 쿼리
        Long totalSize = jpaQueryFactory
                .select(Wildcard.count)
                .from(popup)
                .where(
                        hostEq(host),
                        approvalStatusEq(cond.getApprovalStatus()),
                        popupStatusEq(cond.getPopupStatus())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(popups, pageable, () -> totalSize);
    }

    // 조건 : 호스트
    private BooleanExpression hostEq(Host host) {
        return Objects.nonNull(host) ? popup.host.eq(host) : null;
    }

    // 조건 : 승인 상태
    private BooleanExpression approvalStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                popup.approvalStatus.eq(ApprovalStatus.valueOf(statusBy.toUpperCase())) : null;
    }

    // 조건 : 팝업 상태
    private BooleanExpression popupStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                popup.popupStatus.eq(PopupStatus.valueOf(statusBy.toUpperCase())) : null;
    }
}

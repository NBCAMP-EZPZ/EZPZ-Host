package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.common.dto.PageDto;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzhost.domain.popup.entity.QPopup.popup;

@RequiredArgsConstructor
public class PopupRepositoryQueryImpl implements PopupRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Popup> findAllPopupsByStatus(Host host, PageDto pageDto) {
        JPAQuery<Popup> query = findAllPopupsByStatusQuery(popup, host, pageDto)
                .offset(pageDto.toPageable().getOffset())
                .limit(pageDto.toPageable().getPageSize())
                .orderBy(popup.createdAt.desc());

        List<Popup> popups = query.fetch();
        Long totalSize = countQuery(host, pageDto).fetch().get(0);

        return PageableExecutionUtils.getPage(popups, pageDto.toPageable(), () -> totalSize);
    }

    private <T> JPAQuery<T> findAllPopupsByStatusQuery(Expression<T> expr, Host host, PageDto pageDto) {
        return jpaQueryFactory.select(expr)
                .from(popup)
                .where(
                        hostEq(host),
                        approvalStatusEq(pageDto.getApprovalStatusBy()),
                        popupStatusEq(pageDto.getPopupStatus())
                );
    }

    private JPAQuery<Long> countQuery(Host host, PageDto pageDto) {
        return jpaQueryFactory.select(Wildcard.count)
                .from(popup)
                .where(
                        hostEq(host),
                        approvalStatusEq(pageDto.getApprovalStatusBy()),
                        popupStatusEq(pageDto.getPopupStatus())
                );
    }

    private BooleanExpression hostEq(Host host) {
        return Objects.nonNull(host) ? popup.host.eq(host) : null;
    }

    private BooleanExpression approvalStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                popup.approvalStatus.eq(ApprovalStatus.valueOf(statusBy.toUpperCase())) : null;
    }

    private BooleanExpression popupStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                popup.popupStatus.eq(PopupStatus.valueOf(statusBy.toUpperCase())) : null;
    }
}

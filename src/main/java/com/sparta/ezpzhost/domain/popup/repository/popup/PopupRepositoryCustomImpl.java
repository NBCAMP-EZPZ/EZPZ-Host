package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.common.util.PageUtil;
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
public class PopupRepositoryCustomImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Popup> findAllPopupsByStatus(Host host, PageUtil pageUtil) {
        JPAQuery<Popup> query = findAllPopupsByStatusQuery(popup, host, pageUtil)
                .offset(pageUtil.toPageable().getOffset())
                .limit(pageUtil.toPageable().getPageSize())
                .orderBy(popup.createdAt.desc());

        List<Popup> popups = query.fetch();
        Long totalSize = countQuery(host, pageUtil).fetch().get(0);

        return PageableExecutionUtils.getPage(popups, pageUtil.toPageable(), () -> totalSize);
    }

    private <T> JPAQuery<T> findAllPopupsByStatusQuery(Expression<T> expr, Host host, PageUtil pageUtil) {
        return jpaQueryFactory.select(expr)
                .from(popup)
                .where(
                        hostEq(host),
                        approvalStatusEq(pageUtil.getFirstStatus()),
                        popupStatusEq(pageUtil.getSecondStatus())
                );
    }

    private JPAQuery<Long> countQuery(Host host, PageUtil pageUtil) {
        return jpaQueryFactory.select(Wildcard.count)
                .from(popup)
                .where(
                        hostEq(host),
                        approvalStatusEq(pageUtil.getFirstStatus()),
                        popupStatusEq(pageUtil.getSecondStatus())
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

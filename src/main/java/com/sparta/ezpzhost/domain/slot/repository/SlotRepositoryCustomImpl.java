package com.sparta.ezpzhost.domain.slot.repository;

import static com.sparta.ezpzhost.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzhost.domain.slot.entity.QSlot.slot;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.slot.entity.Slot;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlotRepositoryCustomImpl implements SlotRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	
	@Override
	public Page<Slot> findByPopupId(Long popupId, Pageable pageable) {
		List<Slot> slots = queryFactory
			.selectFrom(slot)
			.where(slot.popup.id.eq(popupId))
			.orderBy(slot.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		
		Long totalCount = queryFactory
			.select(Wildcard.count)
			.from(slot)
			.where(slot.popup.id.eq(popupId))
			.fetchOne();
		
		return new PageImpl<> (slots, pageable, getTotal(totalCount));
	}
}

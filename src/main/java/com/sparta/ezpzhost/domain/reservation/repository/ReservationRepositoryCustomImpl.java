package com.sparta.ezpzhost.domain.reservation.repository;

import static com.sparta.ezpzhost.domain.reservation.entity.QReservation.reservation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzhost.domain.reservation.entity.Reservation;
import com.sparta.ezpzhost.domain.reservation.enums.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<Reservation> findBySlotIdAndReservationStatus(Long slotId, ReservationStatus status) {
		
		return queryFactory
			.selectFrom(reservation)
			.join(reservation.user).fetchJoin()
			.join(reservation.slot).fetchJoin()
			.where(reservation.slot.id.eq(slotId)
				.and(reservation.reservationStatus.eq(status)))
			.fetch();
	}
}

package com.sparta.ezpzhost.domain.reservation.repository;

import java.util.List;

import com.sparta.ezpzhost.domain.reservation.entity.Reservation;
import com.sparta.ezpzhost.domain.reservation.enums.ReservationStatus;

public interface ReservationRepositoryCustom {
	List<Reservation> findBySlotIdAndReservationStatus(Long slotId, ReservationStatus status);
}

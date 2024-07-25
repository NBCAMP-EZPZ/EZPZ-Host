package com.sparta.ezpzhost.domain.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.ezpzhost.domain.reservation.entity.Reservation;
import com.sparta.ezpzhost.domain.reservation.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	@Query("SELECT r FROM Reservation r JOIN FETCH r.user u JOIN FETCH r.slot s WHERE s.id = :slotId AND r.reservationStatus = :status")
	List<Reservation> findBySlotIdAndReservationStatus(@Param("slotId") Long slotId, @Param("status") ReservationStatus status);
}

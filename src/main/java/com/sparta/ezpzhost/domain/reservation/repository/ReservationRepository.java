package com.sparta.ezpzhost.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.ezpzhost.domain.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> , ReservationRepositoryCustom {
}

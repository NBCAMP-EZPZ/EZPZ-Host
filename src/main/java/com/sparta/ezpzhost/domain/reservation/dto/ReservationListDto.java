package com.sparta.ezpzhost.domain.reservation.dto;

import java.util.List;

import com.sparta.ezpzhost.domain.reservation.entity.Reservation;
import com.sparta.ezpzhost.domain.user.entity.User;

import lombok.Getter;

@Getter
public class ReservationListDto {
	private Long id;
	private String name;
	private int numberOfPersons;
	private String phoneNumber;
	
	private ReservationListDto(Reservation reservation, User user) {
		this.id = reservation.getId();
		this.name = user.getName();
		this.numberOfPersons = reservation.getNumberOfPersons();
		this.phoneNumber = user.getPhoneNumber();
	}
	
	public static ReservationListDto of(Reservation reservation, User user) {
		return new ReservationListDto(reservation, user);
	}
	
	public static List<ReservationListDto> listOf(List<Reservation> reservationList) {
		return reservationList.stream()
			.map(reservation -> of(reservation, reservation.getUser()))
			.toList();
	}
}

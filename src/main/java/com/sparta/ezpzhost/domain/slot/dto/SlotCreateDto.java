package com.sparta.ezpzhost.domain.slot.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.sparta.ezpzhost.domain.popup.entity.Popup;

import lombok.Getter;

@Getter
public class SlotCreateDto {
	private LocalDate date;
	private LocalTime time;
	private int availableCount;
	private int totalCount;
	private Popup popup;
	
	
	private SlotCreateDto(LocalDate date, LocalTime time, int availableCount, int totalCount, Popup popup) {
		this.date = date;
		this.time = time;
		this.availableCount = availableCount;
		this.totalCount = totalCount;
		this.popup = popup;
	}
	
	public static SlotCreateDto of(LocalDate date, LocalTime time, int availableCount, int totalCount, Popup popup) {
		return new SlotCreateDto(date, time, availableCount, totalCount, popup);
	}
}

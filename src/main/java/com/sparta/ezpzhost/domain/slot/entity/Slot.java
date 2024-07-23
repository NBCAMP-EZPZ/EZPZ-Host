package com.sparta.ezpzhost.domain.slot.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.slot.enums.SlotStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slot extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "slot_id")
	private Long id;
	
	@Column(nullable = false)
	private LocalDate slotDate;
	
	@Column(nullable = false)
	private LocalTime slotTime;
	
	@Column(nullable = false)
	private int availableCount;
	
	@Column(nullable = false)
	private int totalCount;
	
	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private SlotStatus slotStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "popup_id", nullable = false)
	private Popup popup;
	
	private Slot(LocalDate slotDate, LocalTime slotTime, int availableCount, int totalCount, Popup popup) {
		this.slotDate = slotDate;
		this.slotTime = slotTime;
		this.availableCount = availableCount;
		this.totalCount = totalCount;
		this.slotStatus = SlotStatus.PROCEEDING;
		this.popup = popup;
	}
	
	public static Slot of(LocalDate slotDate, LocalTime slotTime, int availableCount, int totalCount, Popup popup) {
		return new Slot(slotDate, slotTime, availableCount, totalCount, popup);
	}
}

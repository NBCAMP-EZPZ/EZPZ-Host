package com.sparta.ezpzhost.domain.slot.dto;

import com.sparta.ezpzhost.domain.slot.entity.Slot;

import lombok.Getter;

@Getter
public class SlotResponseListDto {
	private Long id;
	private String slotDate;
	private String slotTime;
	private int reservedCount;
	
	private SlotResponseListDto(Slot slot) {
		this.id = slot.getId();
		this.slotDate = slot.getSlotDate().toString();
		this.slotTime = slot.getSlotTime().toString();
		this.reservedCount = slot.getReservedCount();
	}
	
	public static SlotResponseListDto of(Slot slot) {
		return new SlotResponseListDto(slot);
	}
}

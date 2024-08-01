package com.sparta.ezpzhost.domain.slot.dto;

import com.sparta.ezpzhost.domain.slot.entity.Slot;
import java.util.List;
import lombok.Getter;

@Getter
public class SlotResponseDto {

    private Long id;
    private String slotDate;
    private String slotTime;
    private int availableCount;
    private int totalCount;
    private String slotStatus;

    private SlotResponseDto(Slot slot) {
        this.id = slot.getId();
        this.slotDate = slot.getSlotDate().toString();
        this.slotTime = slot.getSlotTime().toString();
        this.availableCount = slot.getAvailableCount();
        this.totalCount = slot.getTotalCount();
        this.slotStatus = slot.getSlotStatus().toString();
    }

    public static SlotResponseDto of(Slot slot) {
        return new SlotResponseDto(slot);
    }

    public static List<SlotResponseDto> listOf(List<Slot> slotList) {
        return slotList.stream()
                .map(SlotResponseDto::of)
                .toList();
    }
}

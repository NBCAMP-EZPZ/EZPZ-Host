package com.sparta.ezpzhost.domain.slot.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.reservation.entity.Reservation;
import com.sparta.ezpzhost.domain.slot.dto.SlotCreateDto;
import com.sparta.ezpzhost.domain.slot.dto.SlotUpdateDto;
import com.sparta.ezpzhost.domain.slot.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "slot", indexes = {
    @Index(name = "idx_popup_slot", columnList = "popup_id, slot_id")
})
public class Slot extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;

    private LocalDate slotDate;

    private LocalTime slotTime;

    private int availableCount;

    private int totalCount;

    private int reservedCount;

    @Enumerated(value = EnumType.STRING)
    private SlotStatus slotStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservation = new ArrayList<>();

    private Slot(SlotCreateDto slotCreateDto) {
        this.slotDate = slotCreateDto.getDate();
        this.slotTime = slotCreateDto.getTime();
        this.availableCount = slotCreateDto.getAvailableCount();
        this.totalCount = slotCreateDto.getTotalCount();
        this.reservedCount = 0;
        this.slotStatus = SlotStatus.PROCEEDING;
        this.popup = slotCreateDto.getPopup();
    }

    public static Slot of(SlotCreateDto slotCreateDto) {
        return new Slot(slotCreateDto);
    }

    public void update(SlotUpdateDto requestDto) {
        this.availableCount = requestDto.getAvailableCount();
        this.totalCount = requestDto.getTotalCount();
        this.slotStatus = SlotStatus.valueOf(requestDto.getSlotStatus());
    }

}

package com.sparta.ezpzhost.domain.slot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.ezpzhost.domain.slot.entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long>, SlotRepositoryCustom {
	boolean existsByPopupId(Long popupId);
	
	Optional<Slot> findByIdAndPopupId(Long slotId, Long popupId);
}

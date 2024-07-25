package com.sparta.ezpzhost.domain.slot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.ezpzhost.domain.slot.entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long>, SlotRepositoryCustom {
	boolean existsByPopupId(Long popupId);
}

package com.sparta.ezpzhost.domain.slot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.ezpzhost.domain.slot.entity.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
	boolean existsByPopupId(Long popupId);
}

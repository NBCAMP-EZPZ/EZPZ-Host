package com.sparta.ezpzhost.domain.slot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.ezpzhost.domain.slot.entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {
	boolean existsByPopupId(Long popupId);
	
	@Query("SELECT s FROM Slot s WHERE s.popup.id = :popupId ORDER BY s.id")
	Page<Slot> findByPopupId(Long popupId, Pageable pageable);
}

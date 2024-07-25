package com.sparta.ezpzhost.domain.slot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.ezpzhost.domain.slot.entity.Slot;

public interface SlotRepositoryCustom {
	Page<Slot> findByPopupId(Long popupId, Pageable pageable);
}

package com.sparta.ezpzhost.domain.popup.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.ezpzhost.domain.popup.entity.Popup;

public interface PopupRepository extends JpaRepository<Popup, Long> {
	Optional<Popup> findByIdAndHostId(Long popupId, Long hostId);
}

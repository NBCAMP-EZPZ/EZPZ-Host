package com.sparta.ezpzhost.domain.popup.repository;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Long> {
}

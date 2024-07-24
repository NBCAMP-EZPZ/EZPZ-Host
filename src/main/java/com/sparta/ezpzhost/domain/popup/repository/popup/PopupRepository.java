package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PopupRepository extends JpaRepository<Popup, Long>, PopupRepositoryCustom {
    Optional<Popup> findByIdAndHostId(Long popupId, Long hostId);
}

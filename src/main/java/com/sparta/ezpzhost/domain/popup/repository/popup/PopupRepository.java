package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Long>, PopupRepositoryCustom {

    Optional<Popup> findByIdAndHostId(Long popupId, Long hostId);

    boolean existsByName(String name);

    List<Popup> findByPopupStatusAndEndDateBefore(PopupStatus popupStatus,
            LocalDateTime localDateTime);
}

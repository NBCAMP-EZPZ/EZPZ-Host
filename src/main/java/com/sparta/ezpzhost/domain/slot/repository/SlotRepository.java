package com.sparta.ezpzhost.domain.slot.repository;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.slot.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long>, SlotRepositoryCustom {

    boolean existsByPopupId(Long popupId);

    Optional<Slot> findByIdAndPopupId(Long slotId, Long popupId);

    Long countByPopup(Popup popup);

}

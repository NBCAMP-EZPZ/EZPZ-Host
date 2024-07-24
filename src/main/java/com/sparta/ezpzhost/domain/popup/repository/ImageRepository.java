package com.sparta.ezpzhost.domain.popup.repository;

import com.sparta.ezpzhost.domain.popup.entity.Image;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPopup(Popup popup);
}

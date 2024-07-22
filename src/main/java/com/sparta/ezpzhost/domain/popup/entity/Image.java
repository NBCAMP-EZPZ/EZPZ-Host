package com.sparta.ezpzhost.domain.popup.entity;

import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="Image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    private Image(Popup popup, String name, String url) {
        this.popup = popup;
        this.name = name;
        this.url = url;
    }

    public static Image of(Popup popup, ImageResponseDto responseDto) {
        return new Image(popup, responseDto.getName(), responseDto.getUrl());
    }
}

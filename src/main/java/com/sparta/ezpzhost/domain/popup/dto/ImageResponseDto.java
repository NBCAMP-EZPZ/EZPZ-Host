package com.sparta.ezpzhost.domain.popup.dto;

import com.sparta.ezpzhost.domain.popup.entity.Image;
import lombok.Getter;

@Getter
public class ImageResponseDto {

    private final String name;
    private final String url;

    public ImageResponseDto(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static ImageResponseDto of(Image image) {
        return new ImageResponseDto(image.getName(), image.getUrl());
    }
}

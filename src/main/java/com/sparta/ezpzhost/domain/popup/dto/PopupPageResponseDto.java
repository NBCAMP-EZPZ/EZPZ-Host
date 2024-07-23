package com.sparta.ezpzhost.domain.popup.dto;

import lombok.Getter;

@Getter
public class PopupPageResponseDto {

    private Long popupId;
    private String name;
    private String companyName;

    private PopupPageResponseDto(Long popupId, String name, String companyName) {
        this.popupId = popupId;
        this.name = name;
        this.companyName = companyName;
    }

    public static PopupPageResponseDto of(Long popupId, String name, String companyName) {
        return new PopupPageResponseDto(popupId, name, companyName);
    }
}

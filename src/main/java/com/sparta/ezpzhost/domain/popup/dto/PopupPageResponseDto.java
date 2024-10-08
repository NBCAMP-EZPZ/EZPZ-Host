package com.sparta.ezpzhost.domain.popup.dto;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import lombok.Getter;

@Getter
public class PopupPageResponseDto {

    private final Long popupId;
    private final String name;
    private final int likeCount;
    private final String companyName;
    private final String thumbnail;

    public PopupPageResponseDto(Popup popup) {
        this.popupId = popup.getId();
        this.name = popup.getName();
        this.likeCount = popup.getLikeCount();
        this.companyName = popup.getHost().getCompanyName();
        this.thumbnail = popup.getThumbnailUrl();
    }

    public static PopupPageResponseDto of(Popup popup) {
        return new PopupPageResponseDto(popup);
    }
}

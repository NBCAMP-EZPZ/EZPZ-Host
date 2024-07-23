package com.sparta.ezpzhost.domain.popup.dto;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PopupResponseDto {

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnailUrl;
    private final String address;
    private final String managerName;
    private final String phoneNumber;
    private final String approvalStatus;
    private final String popupStatus;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final List<String> images;

    private PopupResponseDto(Popup popup, List<String> images) {
        this.id = popup.getId();
        this.name = popup.getName();
        this.description = popup.getDescription();
        this.thumbnailUrl = popup.getThumbnailUrl();
        this.address = popup.getAddress();
        this.managerName = popup.getManagerName();
        this.phoneNumber = popup.getPhoneNumber();
        this.approvalStatus = popup.getApprovalStatus().toString();
        this.popupStatus = popup.getPopupStatus().toString();
        this.startDate = popup.getStartDate();
        this.endDate = popup.getEndDate();
        this.images = images;
    }

    public static PopupResponseDto of(Popup popup, List<String> images) {
        return new PopupResponseDto(popup, images);
    }
}

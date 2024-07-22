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
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private List<String> images;

    private PopupResponseDto(Popup popup) {
        this.id = popup.getId();
        this.name = popup.getName();
        this.description = popup.getDescription();
        this.thumbnailUrl = popup.getThumbnailUrl();
        this.address = popup.getAddress();
        this.managerName = popup.getManagerName();
        this.phoneNumber = popup.getPhoneNumber();
        this.approvalStatus = popup.getApprovalStatus().toString();
        this.startDate = popup.getStartDate();
        this.endDate = popup.getEndDate();
    }

    public static PopupResponseDto of(Popup popup) {
        return new PopupResponseDto(popup);
    }

    public void addImages(List<String> images) {
        this.images = images;
    }
}

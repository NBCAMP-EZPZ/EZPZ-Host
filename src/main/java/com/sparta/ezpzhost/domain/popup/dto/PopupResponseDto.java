package com.sparta.ezpzhost.domain.popup.dto;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PopupResponseDto {

    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private String address;
    private String managerName;
    private String phoneNumber;
    private String approvalStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> images;

    private PopupResponseDto(Popup popup) {
        this.id = popup.getId();
        this.name = popup.getName();
        this.description = popup.getDescription();
        this.thumbnail = popup.getThumbnail();
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

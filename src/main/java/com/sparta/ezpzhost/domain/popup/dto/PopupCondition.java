package com.sparta.ezpzhost.domain.popup.dto;

import lombok.Getter;

@Getter
public class PopupCondition {

    private final String approvalStatus;
    private final String popupStatus;

    private PopupCondition(String approvalStatus, String popupStatus) {
        this.approvalStatus = approvalStatus;
        this.popupStatus = popupStatus;
    }

    public static PopupCondition of(String approvalStatus, String popupStatus) {
        return new PopupCondition(approvalStatus, popupStatus);
    }
}

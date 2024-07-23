package com.sparta.ezpzhost.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // JWT
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요."),
    CARD_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "카드 작성자 및 매니저만 접근할 수 있습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    // Popup
    POPUP_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 팝업이 존재하지 않거나, 팝업에 대한 권한이 없습니다."),
    POPUP_NOT_APPROVAL(HttpStatus.BAD_REQUEST, "승인되지 않은 팝업입니다."),
    
    // Order

    // Item

    // Reservation
    INVALID_DATE_TIME(HttpStatus.BAD_REQUEST, "예약을 등록할 수 있는 날짜, 시간이 아닙니다."),
    SLOT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 예약 슬롯이 존재합니다.");

    //

    private final HttpStatus httpStatus;
    private final String message;

}
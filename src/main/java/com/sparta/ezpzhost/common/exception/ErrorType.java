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

    // Host
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 호스트 아이디입니다."),

    // Popup
    POPUP_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 팝업이 존재하지 않거나, 팝업에 대한 권한이 없습니다."),
    POPUP_CANCEL_FORBIDDEN(HttpStatus.FORBIDDEN, "진행 예정 상태인 팝업만 취소할 수 있습니다."),

    // image
    UPLOAD_FAILED(HttpStatus.GATEWAY_TIMEOUT, "이미지 업로드에 실패하였습니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 유형입니다."),
    IMAGE_LIMIT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "이미지의 최대 용량은 10MB입니다."),
    IMAGE_COUNT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "추가 사진은 최소 1개, 최대 3개까지 등록 가능합니다");

    // Order

    // Item

    // Reservation

    //

    ;
    private final HttpStatus httpStatus;
    private final String message;

}
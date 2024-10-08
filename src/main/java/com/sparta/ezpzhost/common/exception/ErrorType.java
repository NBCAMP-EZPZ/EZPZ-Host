package com.sparta.ezpzhost.common.exception;

import static com.sparta.ezpzhost.common.resolver.CustomPageableHandlerMethodArgumentResolver.MAX_PAGE_SIZE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // Page
    INVALID_PAGE_NUMBER_FORMAT(BAD_REQUEST, "숫자 형식이 아닌 페이지 number입니다."),
    INVALID_PAGE_SIZE_FORMAT(BAD_REQUEST, "숫자 형식이 아닌 페이지 size입니다."),
    INVALID_PAGE_NUMBER(BAD_REQUEST, "페이지 number는 음수일 수 없습니다."),
    INVALID_PAGE_SIZE(BAD_REQUEST, "페이지 size는 0 이하일 수 없습니다."),
    EXCEED_MAX_PAGE_SIZE(BAD_REQUEST, "페이지 size는 " + MAX_PAGE_SIZE + "을 초과할 수 없습니다."),
    EMPTY_PAGE_ELEMENTS(BAD_REQUEST, "페이지의 요소가 존재하지 않습니다."),
    PAGE_NOT_FOUND(BAD_REQUEST, "존재하지 않는 페이지입니다."),

    // JWT
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요."),
    CARD_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "카드 작성자 및 매니저만 접근할 수 있습니다."),

    // Host
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 호스트 아이디입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    // Popup
    POPUP_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 팝업이 존재하지 않거나, 팝업에 대한 권한이 없습니다."),
    POPUP_NOT_APPROVAL(HttpStatus.BAD_REQUEST, "승인되지 않은 팝업입니다."),
    POPUP_CANCEL_FORBIDDEN(HttpStatus.FORBIDDEN, "진행 예정 상태인 팝업만 취소할 수 있습니다."),
    DUPLICATED_POPUP_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 팝업명입니다."),
    POPUP_STATUS_IMPASSIBLE(HttpStatus.BAD_REQUEST, "반려되거나 취소된 팝업은 수정할 수 없습니다."),
    ITEM_REGISTRATION_IMPOSSIBLE(HttpStatus.BAD_REQUEST, "해당 팝업은 굿즈 상품을 등록할 수 없습니다."),


    // image
    UPLOAD_FAILED(HttpStatus.GATEWAY_TIMEOUT, "이미지 업로드에 실패하였습니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 유형입니다."),
    IMAGE_LIMIT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "이미지의 최대 용량은 10MB입니다."),
    IMAGE_COUNT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "추가 사진은 최소 1개, 최대 3개까지 등록 가능합니다"),

    // Order
    INVALID_ORDER_SORT_CONDITION(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 정렬 조건입니다."),
    ORDER_NOT_FOUND_OR_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "해당 주문이 존재하지 않거나, 상품에 대한 권한이 없습니다."),

    // Item
    DUPLICATED_ITEM_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 굿즈명입니다."),
    ITEM_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 상품이 존재하지 않거나, 상품에 대한 권한이 없습니다."),
    ITEM_ALREADY_QUIT(HttpStatus.BAD_REQUEST, "이미 판매 종료된 상품입니다."),
    INVALID_ITEM_STATUS(BAD_REQUEST, "유효하지 않은 상품 상태입니다."),

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약이 존재하지 않습니다."),
    INVALID_DATE_TIME(HttpStatus.BAD_REQUEST, "예약을 등록할 수 있는 날짜, 시간이 아닙니다."),
    SLOT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 예약 슬롯이 존재합니다."),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 슬롯이 존재하지 않습니다."),


    // Page
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "페이지 번호가 올바르지 않습니다."),
    NOT_FOUND_PAGE(HttpStatus.NOT_FOUND, "페이지가 존재하지 않습니다."),

    // SalesStatistics
    STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품의 통계 정보를 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String message;

}
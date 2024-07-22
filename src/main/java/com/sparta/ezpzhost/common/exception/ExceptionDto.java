package com.sparta.ezpzhost.common.exception;

import lombok.Getter;

@Getter
public class ExceptionDto {

    private ErrorType errorType;
    private final String message;

    public ExceptionDto(ErrorType errorType) {
        this.errorType = errorType;
        this.message = errorType.getMessage();
    }

    public ExceptionDto(String message) {
        this.message = message;
    }

}

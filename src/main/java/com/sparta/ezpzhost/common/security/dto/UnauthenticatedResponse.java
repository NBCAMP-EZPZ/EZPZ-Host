package com.sparta.ezpzhost.common.security.dto;

import lombok.Getter;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Getter
public class UnauthenticatedResponse {

    private final int statusCode;
    private final String msg;

    public UnauthenticatedResponse(String msg) {
        this.statusCode = SC_UNAUTHORIZED;
        this.msg = msg;
    }

}

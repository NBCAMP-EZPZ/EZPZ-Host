package com.sparta.ezpzhost.domain.order.controller;

import lombok.Getter;

@Getter
public class OrderRequestDto {

    private String searchType = "all"; // 기본값 설정
    private Long itemId = -1L;         // 기본값 설정
    private String orderStatus = "all"; // 기본값 설정
}

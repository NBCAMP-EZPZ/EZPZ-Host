package com.sparta.ezpzhost.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderRequestDto {

    private String searchType = "all"; // 기본값 설정
    private Long itemId = -1L;         // 기본값 설정
    private String orderStatus = "all"; // 기본값 설정

    public OrderRequestDto() {
        this.searchType = "all";
        this.itemId = -1L;
        this.orderStatus = "all";
    }
}

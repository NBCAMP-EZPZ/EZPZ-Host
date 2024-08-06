package com.sparta.ezpzhost.domain.salesStatistics.controller;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.domain.salesStatistics.service.SalesStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SalesStatisticsController {

    private final SalesStatisticsService salesStatisticsService;

    @GetMapping("/monthly")
    public ResponseEntity<CommonResponse<?>> getMonthlySalesStatistics() {
        return getResponseEntity(salesStatisticsService.getMonthlySalesStatistics(), "월별 매출 조회 성공");
    }

    @GetMapping("/recent-month")
    public ResponseEntity<CommonResponse<?>> getRecentMonthSalesStatistics() {
        return getResponseEntity(salesStatisticsService.getRecentMonthSalesStatistics(),
                "최근 30일 간 매출 조회 성공");
    }
}

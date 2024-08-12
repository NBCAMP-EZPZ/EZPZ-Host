package com.sparta.ezpzhost.domain.salesStatistics.controller;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.domain.salesStatistics.service.SalesStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales-statistics")
public class SalesStatisticsController {

    private final SalesStatisticsService salesStatisticsService;

    @GetMapping("/monthly")
    public ResponseEntity<CommonResponse<?>> getMonthlyItemSalesStatistics(
            @RequestParam Long itemId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return getResponseEntity(
                salesStatisticsService.getMonthlyItemSalesStatistics(itemId,
                        userDetails.getHost()),
                "월별 매출 조회 성공");
    }

    @GetMapping("/recent-month")
    public ResponseEntity<CommonResponse<?>> getDailyPopupSalesStatistics(
            @RequestParam Long popupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return getResponseEntity(
                salesStatisticsService.getDailyPopupSalesStatistics(popupId,
                        userDetails.getHost()),
                "최근 한달 간 매출 조회 성공");
    }
}

package com.sparta.ezpzhost.domain.salesStatistics.dto;

import lombok.Getter;

@Getter
public class RecentMonthSalesStatisticsResponseDto {

    private Long itemId;
    private int totalSalesAmount;
    private int totalSalesCount;

    private RecentMonthSalesStatisticsResponseDto(
            Long itemId,
            int totalSalesAmount,
            int totalSalesCount) {
        this.itemId = itemId;
        this.totalSalesAmount = totalSalesAmount;
        this.totalSalesCount = totalSalesCount;
    }

    public static RecentMonthSalesStatisticsResponseDto of(
            Long itemId,
            int totalSalesAmount,
            int totalSalesCount) {
        return new RecentMonthSalesStatisticsResponseDto(
                itemId,
                totalSalesAmount,
                totalSalesCount
        );
    }
}

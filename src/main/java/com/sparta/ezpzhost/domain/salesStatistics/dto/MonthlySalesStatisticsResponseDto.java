package com.sparta.ezpzhost.domain.salesStatistics.dto;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import lombok.Getter;

@Getter
public class MonthlySalesStatisticsResponseDto {

    private Long itemId;
    private String month;
    private int totalSalesAmount;
    private int totalSalesCount;

    private MonthlySalesStatisticsResponseDto(Long itemId, String month, int totalSalesAmount,
            int totalSalesCount) {
        this.itemId = itemId;
        this.month = month;
        this.totalSalesAmount = totalSalesAmount;
        this.totalSalesCount = totalSalesCount;

    }

    public static MonthlySalesStatisticsResponseDto of(MonthlySalesStatistics statistics) {
        return new MonthlySalesStatisticsResponseDto(
                statistics.getItem().getId(),
                statistics.getMonth(),
                statistics.getTotalSalesAmount(),
                statistics.getTotalSalesCount()
        );
    }
}

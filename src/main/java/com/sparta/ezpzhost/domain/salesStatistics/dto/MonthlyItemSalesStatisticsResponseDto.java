package com.sparta.ezpzhost.domain.salesStatistics.dto;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlyItemSalesStatistics;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MonthlyItemSalesStatisticsResponseDto {

    private Long itemId;
    private int year;
    private int month;
    private int totalSalesCount;

    private MonthlyItemSalesStatisticsResponseDto(Long itemId, int year, int month,
            int totalSalesCount) {
        this.itemId = itemId;
        this.year = year;
        this.month = month;
        this.totalSalesCount = totalSalesCount;
    }

    public static MonthlyItemSalesStatisticsResponseDto of(MonthlyItemSalesStatistics statistics) {
        return new MonthlyItemSalesStatisticsResponseDto(
                statistics.getItem().getId(),
                statistics.getYear(),
                statistics.getMonth(),
                statistics.getTotalSalesCount()
        );
    }
}

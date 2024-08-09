package com.sparta.ezpzhost.domain.salesStatistics.dto;

import com.sparta.ezpzhost.domain.salesStatistics.entity.DailyPopupSalesStatistics;
import lombok.Getter;

@Getter
public class DailyPopupSalesStatisticsResponseDto {

    private Long popupId;
    private int year;
    private int month;
    private int day;
    private int totalSalesAmount;

    public DailyPopupSalesStatisticsResponseDto(Long popupId, int year, int month, int day,
            int totalSalesCount) {
        this.popupId = popupId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.totalSalesAmount = totalSalesCount;
    }

    public static DailyPopupSalesStatisticsResponseDto of(DailyPopupSalesStatistics statistics) {
        return new DailyPopupSalesStatisticsResponseDto(
                statistics.getPopup().getId(),
                statistics.getYear(),
                statistics.getMonth(),
                statistics.getDay(),
                statistics.getTotalSalesAmount()
        );
    }
}
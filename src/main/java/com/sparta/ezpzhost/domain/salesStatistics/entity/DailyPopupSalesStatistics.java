package com.sparta.ezpzhost.domain.salesStatistics.entity;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyPopupSalesStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;

    private int year;
    private int month;
    private int day;

    private int totalSalesAmount;

    private DailyPopupSalesStatistics(Popup popup, int year, int month, int day,
            int totalSalesAmount) {
        this.popup = popup;
        this.year = year;
        this.month = month;
        this.day = day;
        this.totalSalesAmount = totalSalesAmount;
    }

    public static DailyPopupSalesStatistics of(Popup popup, int year, int month, int day,
            int totalSalesAmount) {
        return new DailyPopupSalesStatistics(
                popup, year, month, day, totalSalesAmount
        );
    }
}

package com.sparta.ezpzhost.domain.salesStatistics.entity;

import com.sparta.ezpzhost.domain.item.entity.Item;
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
public class MonthlyItemSalesStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private int year;
    private int month;
    private int totalSalesCount;

    private MonthlyItemSalesStatistics(Item item, int year, int month, int totalSalesCount) {
        this.item = item;
        this.year = year;
        this.month = month;
        this.totalSalesCount = totalSalesCount;
    }

    public static MonthlyItemSalesStatistics of(Item item, int year, int month,
            int totalSalesCount) {
        return new MonthlyItemSalesStatistics(item, year, month, totalSalesCount);
    }
}

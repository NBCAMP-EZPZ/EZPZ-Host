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
public class RecentMonthSalesStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private int totalSalesAmount;
    private int totalSalesCount;

    private RecentMonthSalesStatistics(Item item, int totalSalesAmount, int totalSalesCount) {
        this.item = item;
        this.totalSalesAmount = totalSalesAmount;
        this.totalSalesCount = totalSalesCount;
    }

    public static RecentMonthSalesStatistics of(Item item, int totalSalesAmount,
            int totalSalesCount) {
        return new RecentMonthSalesStatistics(item, totalSalesAmount, totalSalesCount);
    }
}
package com.sparta.ezpzhost.domain.salesStatistics.service;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.entity.RecentMonthSalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.repository.MonthlySalesStatisticsRepository;
import com.sparta.ezpzhost.domain.salesStatistics.repository.RecentMonthSalesStatisticsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesStatisticsService {

    private final MonthlySalesStatisticsRepository monthlySalesStatisticsRepository;

    private final RecentMonthSalesStatisticsRepository recentMonthSalesStatisticsRepository;

    public List<MonthlySalesStatistics> getMonthlySalesStatistics() {
        return monthlySalesStatisticsRepository.findAll();
    }

    public List<RecentMonthSalesStatistics> getRecentMonthSalesStatistics() {
        return recentMonthSalesStatisticsRepository.findAll();
    }
}

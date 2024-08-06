package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.salesStatistics.entity.RecentMonthSalesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentMonthSalesStatisticsRepository extends
        JpaRepository<RecentMonthSalesStatistics, Long> {

}

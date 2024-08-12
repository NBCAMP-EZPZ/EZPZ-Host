package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlyItemSalesStatistics;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyItemSalesStatisticsRepository extends
        JpaRepository<MonthlyItemSalesStatistics, Long> {

    List<MonthlyItemSalesStatistics> findByItemIdOrderByYearDescMonthDesc(Long itemId);

}

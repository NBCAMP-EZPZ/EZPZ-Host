package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySalesStatisticsRepository extends
        JpaRepository<MonthlySalesStatistics, Long> {

    List<MonthlySalesStatistics> findByItemIdOrderByYearDescMonthDesc(Long itemId);

}

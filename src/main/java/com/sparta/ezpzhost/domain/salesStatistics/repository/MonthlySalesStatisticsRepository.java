package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySalesStatisticsRepository extends
        JpaRepository<MonthlySalesStatistics, Long> {

    List<MonthlySalesStatistics> findByItemIdOrderByYearDescMonthDesc(Long itemId);

    Optional<MonthlySalesStatistics> findByItemIdAndYearAndMonth(Long itemId, int year, int month);
}

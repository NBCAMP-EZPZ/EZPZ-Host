package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.salesStatistics.entity.DailyPopupSalesStatistics;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyPopupSalesStatisticsRepository extends
        JpaRepository<DailyPopupSalesStatistics, Long> {

    List<DailyPopupSalesStatistics> findByPopupIdOrderByYearDescMonthDescDayDesc(Long popupId);

}

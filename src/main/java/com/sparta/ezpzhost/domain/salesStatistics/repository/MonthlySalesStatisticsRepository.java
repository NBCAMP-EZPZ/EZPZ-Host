package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySalesStatisticsRepository extends
        JpaRepository<MonthlySalesStatistics, Long>, MonthlySalesStatisticsRepositoryCustom {

}

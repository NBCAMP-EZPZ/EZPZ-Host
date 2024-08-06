package com.sparta.ezpzhost.domain.salesStatistics.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MonthlySalesStatisticsRepositoryCustom {

    Page<MonthlySalesStatistics> findAllMonthlySalesStatisticsByHost(Host host, Pageable pageable);

}

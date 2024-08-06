package com.sparta.ezpzhost.domain.salesStatistics.service;

import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.salesStatistics.dto.MonthlySalesStatisticsResponseDto;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.entity.RecentMonthSalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.repository.MonthlySalesStatisticsRepository;
import com.sparta.ezpzhost.domain.salesStatistics.repository.RecentMonthSalesStatisticsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesStatisticsService {

    private final MonthlySalesStatisticsRepository monthlySalesStatisticsRepository;

    private final RecentMonthSalesStatisticsRepository recentMonthSalesStatisticsRepository;

    public Page<MonthlySalesStatisticsResponseDto> getMonthlySalesStatistics(Pageable pageable,
            Host host) {
        Page<MonthlySalesStatistics> monthlySalesStatistics = monthlySalesStatisticsRepository.findAllMonthlySalesStatisticsByHost(
                host, pageable);
        PageUtil.validatePageableWithPage(pageable, monthlySalesStatistics);
        return monthlySalesStatistics.map(MonthlySalesStatisticsResponseDto::of);
    }

    public List<RecentMonthSalesStatistics> getRecentMonthSalesStatistics() {
        return recentMonthSalesStatisticsRepository.findAll();
    }
}

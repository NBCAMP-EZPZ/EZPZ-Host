package com.sparta.ezpzhost.domain.salesStatistics.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.orderline.entity.Orderline;
import com.sparta.ezpzhost.domain.orderline.repository.OrderlineRepository;
import com.sparta.ezpzhost.domain.salesStatistics.dto.MonthlySalesStatisticsResponseDto;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.repository.MonthlySalesStatisticsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesStatisticsService {

    private final MonthlySalesStatisticsRepository monthlySalesStatisticsRepository;
    private final JobExplorer jobExplorer;
    private final ItemRepository itemRepository;
    private final OrderlineRepository orderlineRepository;


    public List<MonthlySalesStatisticsResponseDto> getMonthlySalesStatistics(
            Long itemId,
            Host host) {
        if (!itemRepository.isItemSoldByHost(itemId, host.getId())) {
            throw new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN);
        }

        List<MonthlySalesStatistics> monthlySalesStatistics = monthlySalesStatisticsRepository.findByItemIdOrderByYearDescMonthDesc(
                itemId);

        List<Orderline> recentOrderLines = orderlineRepository.findRecentOrderLinesByItemId(
                itemId, getLastJobExecutionTime());

        // 통계 업데이트
        List<MonthlySalesStatistics> updatedStatisticsList = updateMonthlySalesStatistics(itemId,
                monthlySalesStatistics, recentOrderLines);

        return updatedStatisticsList.stream().map(MonthlySalesStatisticsResponseDto::of).collect(
                Collectors.toList());
    }

//    public RecentMonthSalesStatisticsResponseDto getRecentMonthSalesStatistics(Long itemId,
//            Host host) {
//        if (!itemRepository.isItemSoldByHost(itemId, host.getId())) {
//            throw new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN);
//        }
//
//        RecentMonthSalesStatistics recentMonthSalesStatistics = recentMonthSalesStatisticsRepository.findByItemId(
//                itemId).orElseThrow(() -> new CustomException(ErrorType.STATISTICS_NOT_FOUND));
//
//        List<Orderline> recentOrderLines = orderlineRepository.findRecentOrderLinesByItemId(
//                itemId, getLastJobExecutionTime());
//
//        int additionalSalesAmount = recentOrderLines.stream().mapToInt(Orderline::getOrderPrice)
//                .sum() + recentMonthSalesStatistics.getTotalSalesAmount();
//        int additionalSalesCount = recentOrderLines.stream().mapToInt(Orderline::getQuantity).sum()
//                + recentMonthSalesStatistics.getTotalSalesCount();
//
//        return RecentMonthSalesStatisticsResponseDto.of(itemId, additionalSalesAmount,
//                additionalSalesCount);
//    }

    /* UTIL */
    private LocalDateTime getLastJobExecutionTime() {
        return jobExplorer.getJobInstances("salesStatisticsJob", 0, 1)
                .stream()
                .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                .filter(jobExecution -> jobExecution.getStatus() == BatchStatus.COMPLETED)
                .map(jobExecution -> Optional.ofNullable(jobExecution.getEndTime())
                        .map(endTime -> ((LocalDateTime) endTime).toInstant(ZoneOffset.UTC)
                                .atZone(ZoneOffset.UTC).toLocalDateTime())
                        .orElse(null))
                .filter(Objects::nonNull) // null 값 필터링
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusDays(1)); // 예시로 하루 전 시간을 반환하도록 설정
    }

    private List<MonthlySalesStatistics> updateMonthlySalesStatistics(Long itemId,
            List<MonthlySalesStatistics> statisticsList, List<Orderline> recentOrderLines) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        Optional<MonthlySalesStatistics> monthlySalesStatistics = monthlySalesStatisticsRepository.findByItemIdAndYearAndMonth(
                itemId, currentYear, currentMonth);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN));

        int salesAmount = recentOrderLines.stream().mapToInt(Orderline::getOrderPrice).sum();
        int salesCount = recentOrderLines.stream().mapToInt(Orderline::getQuantity).sum();

        MonthlySalesStatistics newMonthlySalesStatistics;

        LinkedList<MonthlySalesStatistics> updatedStatisticsList = new LinkedList<>(statisticsList);

        if (monthlySalesStatistics.isEmpty()) {
            newMonthlySalesStatistics = MonthlySalesStatistics.of(item, currentYear, currentMonth,
                    salesCount);
        } else {
            newMonthlySalesStatistics = MonthlySalesStatistics.of(item, currentYear, currentMonth,
                    salesCount + monthlySalesStatistics.get()
                            .getTotalSalesCount());
            updatedStatisticsList.removeFirst();
        }

        updatedStatisticsList.addFirst(newMonthlySalesStatistics);

        return updatedStatisticsList;
    }
}

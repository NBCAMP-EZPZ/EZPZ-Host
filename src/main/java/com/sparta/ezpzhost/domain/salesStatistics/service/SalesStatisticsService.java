package com.sparta.ezpzhost.domain.salesStatistics.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.orderline.entity.Orderline;
import com.sparta.ezpzhost.domain.orderline.repository.OrderlineRepository;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzhost.domain.salesStatistics.dto.DailyPopupSalesStatisticsResponseDto;
import com.sparta.ezpzhost.domain.salesStatistics.dto.MonthlySalesStatisticsResponseDto;
import com.sparta.ezpzhost.domain.salesStatistics.entity.DailyPopupSalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.repository.DailyPopupSalesStatisticsRepository;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesStatisticsService {

    private final MonthlySalesStatisticsRepository monthlySalesStatisticsRepository;
    private final DailyPopupSalesStatisticsRepository dailyPopupSalesStatisticsRepository;
    private final JobExplorer jobExplorer;
    private final ItemRepository itemRepository;
    private final OrderlineRepository orderlineRepository;
    private final PopupRepository popupRepository;


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

    public List<DailyPopupSalesStatisticsResponseDto> getDailyPopupSalesStatistics(Long popupId,
            Host host) {
        Popup popup = popupRepository.findByIdAndHostId(popupId, host.getId())
                .orElseThrow(() -> new CustomException(ErrorType.POPUP_ACCESS_FORBIDDEN));

        List<DailyPopupSalesStatistics> dailyPopupSalesStatistics = dailyPopupSalesStatisticsRepository.findByPopupIdOrderByYearDescMonthDescDayDesc(
                popupId);
        List<Orderline> recentOrderlines = orderlineRepository.findRecentOrderLinesByPopupId(
                popupId, getLastJobExecutionTime());

        DailyPopupSalesStatistics newDailyPopupSalesStatistics = calculateTodaySales(popup,
                recentOrderlines);
        dailyPopupSalesStatistics.add(0, newDailyPopupSalesStatistics);

        return dailyPopupSalesStatistics.stream().map(DailyPopupSalesStatisticsResponseDto::of)
                .collect(Collectors.toList());
    }

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
                .orElse(LocalDateTime.now().minusDays(1)); // 하루 전 시간을 반환하도록 설정
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

    private DailyPopupSalesStatistics calculateTodaySales(Popup popup,
            List<Orderline> recentOrderLines) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        int currentDay = currentDate.getDayOfMonth();

        int totalSalesAmount = recentOrderLines.stream().mapToInt(Orderline::getOrderPrice).sum();

        return DailyPopupSalesStatistics.of(popup, currentYear, currentMonth, currentDay,
                totalSalesAmount);
    }
}

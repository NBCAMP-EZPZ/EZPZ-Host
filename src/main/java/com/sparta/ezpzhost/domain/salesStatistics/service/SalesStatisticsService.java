package com.sparta.ezpzhost.domain.salesStatistics.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzhost.domain.salesStatistics.dto.DailyPopupSalesStatisticsResponseDto;
import com.sparta.ezpzhost.domain.salesStatistics.dto.MonthlySalesStatisticsResponseDto;
import com.sparta.ezpzhost.domain.salesStatistics.entity.DailyPopupSalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.repository.DailyPopupSalesStatisticsRepository;
import com.sparta.ezpzhost.domain.salesStatistics.repository.MonthlySalesStatisticsRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesStatisticsService {

    private final MonthlySalesStatisticsRepository monthlySalesStatisticsRepository;
    private final DailyPopupSalesStatisticsRepository dailyPopupSalesStatisticsRepository;
    private final ItemRepository itemRepository;
    private final PopupRepository popupRepository;

    /**
     * 각 상품의 월별 판매량 통계 조회
     *
     * @param itemId
     * @param host
     * @return
     */
    public List<MonthlySalesStatisticsResponseDto> getMonthlySalesStatistics(
            Long itemId,
            Host host) {
        if (!itemRepository.existsByIdAndHostId(itemId, host.getId())) {
            throw new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN);
        }

        List<MonthlySalesStatistics> monthlySalesStatistics = monthlySalesStatisticsRepository.findByItemIdOrderByYearDescMonthDesc(
                itemId);

        return monthlySalesStatistics.stream().map(MonthlySalesStatisticsResponseDto::of).collect(
                Collectors.toList());
    }

    /**
     * 각 팝업의 최근 한달 간 일별 매출액 통계 조회
     *
     * @param popupId
     * @param host
     * @return
     */
    public List<DailyPopupSalesStatisticsResponseDto> getDailyPopupSalesStatistics(Long popupId,
            Host host) {
        Popup popup = popupRepository.findByIdAndHostId(popupId, host.getId())
                .orElseThrow(() -> new CustomException(ErrorType.POPUP_ACCESS_FORBIDDEN));

        List<DailyPopupSalesStatistics> dailyPopupSalesStatistics = dailyPopupSalesStatisticsRepository.findByPopupIdOrderByYearDescMonthDescDayDesc(
                popupId);

        return dailyPopupSalesStatistics.stream().map(DailyPopupSalesStatisticsResponseDto::of)
                .collect(Collectors.toList());
    }

}

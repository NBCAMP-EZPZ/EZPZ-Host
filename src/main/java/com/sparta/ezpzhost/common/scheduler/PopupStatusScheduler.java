package com.sparta.ezpzhost.common.scheduler;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PopupStatusScheduler {

    private final PopupRepository popupRepository;

    /**
     * 종료일이 지난 팝업스토어의 상태를 변경하는 스케줄러(매일 01시에 수행)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void updatePopupStoreStatus() {
        List<Popup> activeStores = popupRepository.findByPopupStatusAndEndDateBefore(
                PopupStatus.IN_PROGRESS, LocalDateTime.now());

        for (Popup popup : activeStores) {
            popup.completePopup();
        }
    }
}

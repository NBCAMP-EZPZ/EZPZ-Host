//package com.sparta.ezpzhost.domain.slot.service;
//
//import com.sparta.ezpzhost.domain.host.entity.Host;
//import com.sparta.ezpzhost.domain.host.repository.HostRepository;
//import com.sparta.ezpzhost.domain.popup.entity.Popup;
//import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
//import com.sparta.ezpzhost.domain.slot.dto.SlotRequestDto;
//import com.sparta.ezpzhost.domain.slot.repository.SlotRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//public class SlotConcurrencyTest {
//
//    @Autowired
//    SlotService slotService;
//
//    @Autowired
//    SlotRepository slotRepository;
//
//    @Autowired
//    PopupRepository popupRepository;
//
//    @Autowired
//    HostRepository hostRepository;
//
//    Host host;
//    Popup popup;
//    SlotRequestDto slotRequestDto;
//
//    int threadCount = 100;
//
//    @BeforeEach
//    void setUp() {
//        host = Host.createMockHost("mockHost");
//        hostRepository.save(host);
//
//        LocalDateTime startDate = LocalDateTime.of(2024, 8, 1, 12, 0);
//        LocalDateTime endDate = LocalDateTime.of(2024, 8, 2, 18, 0);
//
//        popup = Popup.createMockPopup(host, startDate, endDate);
//        popupRepository.save(popup);
//
//        slotRequestDto = new SlotRequestDto(
//                LocalDate.of(2024, 8, 1),
//                LocalDate.of(2024, 8, 2),
//                LocalTime.of(12, 0),
//                LocalTime.of(15, 0),
//                1,
//                10
//        );
//    }
//
//    @Test
//    @DisplayName("실패 - 슬롯 생성 동시성 테스트")
//    void createSlotWithoutLock() throws InterruptedException {
//        // given
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        // when
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    slotService.createSlotWithoutLock(popup.getId(), slotRequestDto, host);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await();
//
//        // then
//        Long totalSlotCount = slotRepository.countByPopup(popup);
//        assertThat(totalSlotCount).isNotZero().isNotEqualTo(8);
//
//        System.out.println("\n[totalSlotCount]");
//        System.out.println("Expected = 8");
//        System.out.println("Actual = " + totalSlotCount);
//    }
//
//    @Test
//    @DisplayName("성공 - 슬롯 생성 동시성 테스트")
//    void createSlot() throws InterruptedException {
//        // given
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        // when
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    slotService.createSlot(popup.getId(), slotRequestDto, host);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await();
//
////        IntStream.range(0, threadCount).parallel().forEach(i ->
////                slotService.createSlot(popup.getId(), slotRequestDto, host)
////        );
//
//        // then
//        Long totalSlotCount = slotRepository.countByPopup(popup);
//        assertThat(totalSlotCount).isEqualTo(8);
//
//        System.out.println("\n[totalSlotCount]");
//        System.out.println("Expected = 8");
//        System.out.println("Actual = " + totalSlotCount);
//    }
//
//
//}

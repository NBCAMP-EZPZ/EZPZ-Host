package com.sparta.ezpzhost.common.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class DailyPopupSalesScheduler {

    private final JobLauncher jobLauncher;
    private final Job dailyPopupSalesJob;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void runDailyPopupSalesJob() {
        try {
            String targetDate = LocalDate.now().minusDays(1).toString();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetDate", targetDate) // 전날 날짜 설정
                    .addLong("run.id", System.currentTimeMillis()) // 고유한 ID 생성
                    .toJobParameters();

            jobLauncher.run(dailyPopupSalesJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    // 초기 데이터를 만들기 위해 한 달 전부터 현재까지의 데이터를 계산하는 메서드
//    @Scheduled(cron = "0 8 1 * * ?")
//    public void runDailyPopupSalesJobForLastMonth() {
//        try {
//            LocalDate startDate = LocalDate.now().minusMonths(1);  // 한 달 전 날짜
//            LocalDate endDate = LocalDate.now().minusDays(1);  // 어제 날짜
//
//            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
//                String targetDate = date.toString();
//
//                JobParameters jobParameters = new JobParametersBuilder()
//                        .addString("targetDate", targetDate) // 날짜 설정
//                        .addLong("run.id", System.currentTimeMillis()) // 고유한 ID 생성
//                        .toJobParameters();
//
//                jobLauncher.run(dailyPopupSalesJob, jobParameters);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}

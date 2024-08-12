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
public class MonthlyItemSalesScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyItemSalesJob;

    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 실행
    public void runMonthlyItemSalesJob() {
        try {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String year = String.valueOf(lastMonth.getYear());
            String month = String.valueOf(lastMonth.getMonthValue());

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("year", year)
                    .addString("month", month)
                    .addLong("run.id", System.currentTimeMillis()) // 고유한 ID 생성
                    .toJobParameters();

            jobLauncher.run(monthlyItemSalesJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2023년 1월부터 전월까지의 데이터를 생성하기 위한 메서드
    @Scheduled(cron = "0 35 23 * * ?")
    public void runMonthlyItemSalesJobForInitialData() {
        try {
            LocalDate startDate = LocalDate.of(2023, 1, 1);  // 2023년 1월 1일
            LocalDate endDate = LocalDate.now().minusMonths(1);  // 현재 날짜의 전월

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusMonths(1)) {
                String year = String.valueOf(date.getYear());
                String month = String.valueOf(date.getMonthValue());

                JobParameters jobParameters = new JobParametersBuilder()
                        .addString("year", year)
                        .addString("month", month)
                        .addLong("run.id", System.currentTimeMillis()) // 고유한 ID 생성
                        .toJobParameters();

                jobLauncher.run(monthlyItemSalesJob, jobParameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

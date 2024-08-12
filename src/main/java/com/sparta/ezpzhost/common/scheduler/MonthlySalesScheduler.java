package com.sparta.ezpzhost.common.scheduler;

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
public class MonthlySalesScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyItemSalesJob;

    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 실행
    public void runMonthlyItemSalesJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis()) // 고유한 ID 생성
                    .toJobParameters();

            jobLauncher.run(monthlyItemSalesJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

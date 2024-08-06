package com.sparta.ezpzhost.common.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job salesStatisticsJob;

    @Scheduled(cron = "0 1 17 * * ?") // 매일 오후 12시 58분에 실행
    public void runSalesStatisticsJob() {
        try {
            jobLauncher.run(salesStatisticsJob, new JobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

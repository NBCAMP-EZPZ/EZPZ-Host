package com.sparta.ezpzhost.common.scheduler;

import com.sparta.ezpzhost.common.config.JobConfig;
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
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job salesStatisticsJob;
    private final JobConfig jobConfig;

    @Scheduled(cron = "0 17 13 * * ?") // 매일 오후 12시 58분에 실행
    public void runSalesStatisticsJob() {
        if (jobConfig.isDataChanged()) {
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis()) // 매번 다른 값을 추가하여 고유하게 설정
                        .toJobParameters();
                jobLauncher.run(salesStatisticsJob, jobParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No data changes detected. Job execution skipped.");
        }
    }
}

package com.sparta.ezpzhost.common.scheduler;

import com.sparta.ezpzhost.common.config.JobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job salesStatisticsJob;
    private final JobConfig jobConfig;

//    @Scheduled(cron = "0 0 0 * * ?")
//    public void runSalesStatisticsJob() {
//        // 새로 들어온 데이터가 있을 때에만 Job을 수행하기 위한 로직
//        if (jobConfig.isDataChanged()) {
//            try {
//                JobParameters jobParameters = new JobParametersBuilder()
//                        .addLong("run.id", System.currentTimeMillis()) // 매번 다른 값을 추가하여 고유하게 설정
//                        .toJobParameters();
//                jobLauncher.run(salesStatisticsJob, jobParameters);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("No data changes detected. Job execution skipped.");
//        }
//    }
}

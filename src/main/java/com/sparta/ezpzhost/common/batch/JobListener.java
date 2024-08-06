package com.sparta.ezpzhost.common.batch;

import java.time.LocalDateTime;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.setStartTime(LocalDateTime.now());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        jobExecution.setEndTime(LocalDateTime.now());
    }
}

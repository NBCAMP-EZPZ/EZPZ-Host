package com.sparta.ezpzhost.common.config.batch;

import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzhost.domain.salesStatistics.entity.DailyPopupSalesStatistics;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DailyPopupSalesJobConfig {

    private final DataSource dataSource;
    private final PopupRepository popupRepository;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job dailyPopupSalesJob(JobRepository jobRepository, Step dailyPopupSalesStep,
            Step deleteOldSalesStep) {
        return new JobBuilder("dailyPopupSalesJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 배치 작업의 실행마다 고유한 ID를 생성
                .start(dailyPopupSalesStep)
                .next(deleteOldSalesStep)
                .build();
    }

    @Bean
    public Step dailyPopupSalesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailyPopupSalesStep", jobRepository)
                .<Map<String, Object>, DailyPopupSalesStatistics>chunk(100, transactionManager)
                .reader(dailyPopupSalesReader(null))
                .processor(dailyPopupSalesProcessor())
                .writer(dailyPopupSalesWriter())
                .build();
    }

    /* 최근 한달 간의 일별 통계 조회를 위해서 가장 오래된 통계 자료 삭제 */
    @Bean
    public Step deleteOldSalesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteOldSalesStep", jobRepository)
                .tasklet(deleteOldSalesTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet deleteOldSalesTasklet() {
        return (contribution, chunkContext) -> {
            jdbcTemplate.update(
                    "DELETE FROM daily_popup_sales_statistics "
                            + "WHERE (year < YEAR(CURDATE() - INTERVAL 1 MONTH - INTERVAL 1 DAY)) "
                            + "OR (year = YEAR(CURDATE() - INTERVAL 1 MONTH - INTERVAL 1 DAY) "
                            + "AND month < MONTH(CURDATE() - INTERVAL 1 MONTH - INTERVAL 1 DAY)) "
                            + "OR (year = YEAR(CURDATE() - INTERVAL 1 MONTH - INTERVAL 1 DAY) "
                            + "AND month = MONTH(CURDATE() - INTERVAL 1 MONTH - INTERVAL 1 DAY) "
                            + "AND day < DAY(CURDATE() - INTERVAL 1 MONTH - INTERVAL 1 DAY))");

            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, Object>> dailyPopupSalesReader(
            @Value("#{jobParameters['targetDate']}") String targetDate) {
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .name("dailyPopupSalesReader")
                .sql("SELECT i.popup_id AS popup_id, YEAR(o.modified_at) AS year, MONTH(o.modified_at) AS month, DAY(o.modified_at) AS day, SUM(ol.order_price) AS total_sales_amount "
                        + "FROM orderline ol "
                        + "JOIN orders o ON ol.order_id = o.order_id "
                        + "JOIN item i ON ol.item_id = i.item_id "
                        + "WHERE o.order_status = 'ORDER_COMPLETED' "
                        + "AND DATE(o.modified_at) = ? "  // 위치 기반의 파라미터로 수정
                        + "GROUP BY i.popup_id, YEAR(o.modified_at), MONTH(o.modified_at), DAY(o.modified_at)")
                .rowMapper(new ColumnMapRowMapper())
                .preparedStatementSetter(
                        new ArgumentPreparedStatementSetter(
                                new Object[]{targetDate}))  // targetDate 값을 PreparedStatement에 설정
                .build();
    }


    @Bean
    public ItemProcessor<Map<String, Object>, DailyPopupSalesStatistics> dailyPopupSalesProcessor() {
        return resultMap -> {
            Long popupId = (Long) resultMap.get("popup_id");
            Popup popup = popupRepository.findById(popupId).orElseThrow(
                    () -> new IllegalArgumentException("Invalid popup ID: " + popupId));

            int year = ((Number) resultMap.get("year")).intValue();
            int month = ((Number) resultMap.get("month")).intValue();
            int day = ((Number) resultMap.get("day")).intValue();
            int totalSalesAmount = ((Number) resultMap.get("total_sales_amount")).intValue();

            return DailyPopupSalesStatistics.of(popup, year, month, day, totalSalesAmount);
        };
    }

    @Bean
    public JdbcBatchItemWriter<DailyPopupSalesStatistics> dailyPopupSalesWriter() {
        return new JdbcBatchItemWriterBuilder<DailyPopupSalesStatistics>()
                .dataSource(dataSource)
                .sql("INSERT INTO daily_popup_sales_statistics (popup_id, year, month, day, total_sales_amount) VALUES (?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE total_sales_amount = VALUES(total_sales_amount)")
                .itemPreparedStatementSetter(
                        new ItemPreparedStatementSetter<DailyPopupSalesStatistics>() {
                            @Override
                            public void setValues(DailyPopupSalesStatistics item,
                                    PreparedStatement ps)
                                    throws SQLException {
                                ps.setLong(1, item.getPopup().getId());
                                ps.setInt(2, item.getYear());
                                ps.setInt(3, item.getMonth());
                                ps.setInt(4, item.getDay());
                                ps.setInt(5, item.getTotalSalesAmount());
                            }
                        })
                .build();
    }
}

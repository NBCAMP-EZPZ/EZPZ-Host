package com.sparta.ezpzhost.common.config;

import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.order.repository.OrderRepository;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzhost.domain.salesStatistics.entity.DailyPopupSalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private final DataSource dataSource;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PopupRepository popupRepository;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job salesStatisticsJob(JobRepository jobRepository, Step monthlySalesStep,
            Step deleteOldSalesStep,
            Step dailyPopupSalesStep) {
        return new JobBuilder("salesStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 배치 작업의 실행마다 고유한 ID를 생성
                .start(monthlySalesStep)
                .next(deleteOldSalesStep)
                .next(dailyPopupSalesStep)
                .build();
    }

    @Bean
    public Step monthlySalesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("monthlySalesStep", jobRepository)
                .<Map<String, Object>, MonthlySalesStatistics>chunk(100, transactionManager)
                .reader(monthlySalesReader())
                .processor(monthlySalesProcessor())
                .writer(monthlySalesWriter())
                .build();
    }

    @Bean
    public Step dailyPopupSalesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailyPopupSalesStep", jobRepository)
                .<Map<String, Object>, DailyPopupSalesStatistics>chunk(100, transactionManager)
                .reader(dailyPopupSalesReader())
                .processor(dailyPopupSalesProcessor())
                .writer(dailyPopupSalesWriter())
                .build();
    }

    /**
     * 최근 한달 간의 일별 통계 조회를 위해서 가장 오래된 통계 자료 삭제
     *
     * @param jobRepository
     * @param transactionManager
     * @return
     */
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
                            + "WHERE (year < YEAR(CURDATE() - INTERVAL 1 MONTH)) "
                            + "OR (year = YEAR(CURDATE() - INTERVAL 1 MONTH) AND month < MONTH(CURDATE() - INTERVAL 1 MONTH)) "
                            + "OR (year = YEAR(CURDATE() - INTERVAL 1 MONTH) AND month = MONTH(CURDATE() - INTERVAL 1 MONTH) AND day < DAY(CURDATE() - INTERVAL 1 MONTH))");

            return RepeatStatus.FINISHED;
        };
    }

    /* 각 상품의 월별 판매량 통계 조회를 위한 Reader, Processor, Writer */

    @Bean
    public JdbcCursorItemReader<Map<String, Object>> monthlySalesReader() {
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .name("monthlySalesReader")
                .sql("SELECT item_id, YEAR(orders.modified_at) AS year, MONTH(orders.modified_at) AS month, SUM(orderline.quantity) AS total_sales_count "
                        +
                        "FROM orderline JOIN orders ON orderline.order_id = orders.order_id " +
                        "WHERE orders.order_status = 'ORDER_COMPLETED' " +
                        "GROUP BY item_id, year, month")
                .rowMapper(new ColumnMapRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Map<String, Object>, MonthlySalesStatistics> monthlySalesProcessor() {
        return resultMap -> {
            Long itemId = (Long) resultMap.get("item_id");
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + itemId));

            int year = ((Number) resultMap.get("year")).intValue();
            int month = ((Number) resultMap.get("month")).intValue();
            int totalSalesCount = ((Number) resultMap.get("total_sales_count")).intValue();

            return MonthlySalesStatistics.of(item, year, month, totalSalesCount);
        };
    }

    @Bean
    public JdbcBatchItemWriter<MonthlySalesStatistics> monthlySalesWriter() {
        return new JdbcBatchItemWriterBuilder<MonthlySalesStatistics>()
                .dataSource(dataSource)
                .sql("INSERT INTO monthly_sales_statistics (item_id, year, month, total_sales_count) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE total_sales_count = VALUES(total_sales_count)")
                .itemPreparedStatementSetter(
                        new ItemPreparedStatementSetter<MonthlySalesStatistics>() {
                            @Override
                            public void setValues(MonthlySalesStatistics item, PreparedStatement ps)
                                    throws SQLException {
                                ps.setLong(1, item.getItem().getId());
                                ps.setInt(2, item.getYear());
                                ps.setInt(3, item.getMonth());
                                ps.setInt(4, item.getTotalSalesCount());
                            }
                        })
                .build();
    }

    /* 각 팝업의 최근 한달 간 일별 매출액 통계 조회를 위한 Reader, Processor, Writer */

    @Bean
    public JdbcCursorItemReader<Map<String, Object>> dailyPopupSalesReader() {
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .name("dailyPopupSalesReader")
                .sql("SELECT i.popup_id AS popup_id, YEAR(o.modified_at) AS year, MONTH(o.modified_at) AS month, DAY(o.modified_at) AS day, SUM(ol.order_price) AS total_sales_amount "
                        +
                        "FROM orderline ol " +
                        "JOIN orders o ON ol.order_id = o.order_id " +
                        "JOIN item i ON ol.item_id = i.item_id " +
                        "WHERE o.order_status = 'ORDER_COMPLETED' " +
                        "AND o.modified_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
                        "GROUP BY i.popup_id, YEAR(o.modified_at), MONTH(o.modified_at), DAY(o.modified_at)")
                .rowMapper(new ColumnMapRowMapper())
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
                        +
                        "ON DUPLICATE KEY UPDATE total_sales_amount = VALUES(total_sales_amount)")
                .itemPreparedStatementSetter(
                        new ItemPreparedStatementSetter<DailyPopupSalesStatistics>() {
                            @Override
                            public void setValues(DailyPopupSalesStatistics item,
                                    PreparedStatement ps) throws SQLException {
                                ps.setLong(1, item.getPopup().getId());
                                ps.setInt(2, item.getYear());
                                ps.setInt(3, item.getMonth());
                                ps.setInt(4, item.getDay());
                                ps.setInt(5, item.getTotalSalesAmount());
                            }
                        })
                .build();
    }

    /* UTIL */

    /**
     * 데이터 변경 여부를 확인하는 로직
     *
     * @return
     */
    public boolean isDataChanged() {
        return orderRepository.existsByModifiedAtAfter(getLastJobExecutionTime()) ||
                itemRepository.existsByModifiedAtAfter(getLastJobExecutionTime());
    }

    /**
     * 마지막으로 성공한 배치 작업의 실행 시간을 반환
     *
     * @return
     */
    private LocalDateTime getLastJobExecutionTime() {
        return LocalDateTime.now().minusDays(1);
    }
}

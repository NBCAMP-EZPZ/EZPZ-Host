package com.sparta.ezpzhost.common.config;

import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.order.repository.OrderRepository;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlySalesStatistics;
import com.sparta.ezpzhost.domain.salesStatistics.entity.RecentMonthSalesStatistics;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private final DataSource dataSource;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Bean
    public Job salesStatisticsJob(JobRepository jobRepository, Step monthlySalesStep,
            Step recentMonthSalesStep) {
        return new JobBuilder("salesStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 배치 작업의 실행마다 고유한 ID를 생성
                .start(monthlySalesStep)
                .next(recentMonthSalesStep)
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
    public Step recentMonthSalesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("recentMonthSalesStep", jobRepository)
                .<Map<String, Object>, RecentMonthSalesStatistics>chunk(100, transactionManager)
                .reader(recentMonthSalesReader())
                .processor(recentMonthSalesProcessor())
                .writer(recentMonthSalesWriter())
                .build();
    }

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


    @Bean
    public JdbcCursorItemReader<Map<String, Object>> recentMonthSalesReader() {
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .name("recentMonthSalesReader")
                .sql("SELECT item_id, SUM(orderline.order_price) AS total_sales_amount, SUM(orderline.quantity) AS total_sales_count "
                        +
                        "FROM orderline JOIN orders ON orderline.order_id = orders.order_id " +
                        "WHERE orders.order_status = 'ORDER_COMPLETED' AND orders.modified_at > DATE_SUB(NOW(), INTERVAL 1 MONTH) "
                        +
                        "GROUP BY item_id")
                .rowMapper(new ColumnMapRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Map<String, Object>, RecentMonthSalesStatistics> recentMonthSalesProcessor() {
        return resultMap -> {
            Long itemId = (Long) resultMap.get("item_id");
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + itemId));

            int totalSalesAmount = ((Number) resultMap.get("total_sales_amount")).intValue();
            int totalSalesCount = ((Number) resultMap.get("total_sales_count")).intValue();

            return RecentMonthSalesStatistics.of(item, totalSalesAmount, totalSalesCount);
        };
    }

    @Bean
    public JdbcBatchItemWriter<RecentMonthSalesStatistics> recentMonthSalesWriter() {
        return new JdbcBatchItemWriterBuilder<RecentMonthSalesStatistics>()
                .dataSource(dataSource)
                .sql("INSERT INTO recent_month_sales_statistics (item_id, total_sales_amount, total_sales_count) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE total_sales_amount = VALUES(total_sales_amount), total_sales_count = VALUES(total_sales_count)")
                .itemPreparedStatementSetter(
                        new ItemPreparedStatementSetter<RecentMonthSalesStatistics>() {
                            @Override
                            public void setValues(RecentMonthSalesStatistics item,
                                    PreparedStatement ps) throws SQLException {
                                ps.setLong(1, item.getItem().getId());
                                ps.setInt(2, item.getTotalSalesAmount());
                                ps.setInt(3, item.getTotalSalesCount());
                            }
                        })
                .build();
    }

    public boolean isDataChanged() {
        // 데이터 변경 여부를 확인하는 로직
        return orderRepository.existsByModifiedAtAfter(getLastJobExecutionTime()) ||
                itemRepository.existsByModifiedAtAfter(getLastJobExecutionTime());
    }

    private LocalDateTime getLastJobExecutionTime() {
        // 마지막으로 성공한 배치 작업의 실행 시간을 반환
        return LocalDateTime.now().minusDays(1);
    }
}

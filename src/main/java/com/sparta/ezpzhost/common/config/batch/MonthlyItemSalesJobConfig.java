package com.sparta.ezpzhost.common.config.batch;

import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.salesStatistics.entity.MonthlyItemSalesStatistics;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class MonthlyItemSalesJobConfig {

    private final DataSource dataSource;
    private final ItemRepository itemRepository;

    @Bean
    public Job monthlyItemSalesJob(JobRepository jobRepository, Step monthlyItemSalesStep) {
        return new JobBuilder("monthlyItemSalesJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 배치 작업의 실행마다 고유한 ID를 생성
                .start(monthlyItemSalesStep)
                .build();
    }

    @Bean
    public Step monthlyItemSalesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("monthlyItemSalesStep", jobRepository)
                .<Map<String, Object>, MonthlyItemSalesStatistics>chunk(100, transactionManager)
                .reader(monthlyItemSalesReader())
                .processor(monthlyItemSalesProcessor())
                .writer(monthlyItemSalesWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Map<String, Object>> monthlyItemSalesReader() {
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .name("monthlyItemSalesReader")
                .sql("SELECT item_id, YEAR(orders.modified_at) AS year, MONTH(orders.modified_at) AS month, SUM(orderline.quantity) AS total_sales_count "
                        + "FROM orderline JOIN orders ON orderline.order_id = orders.order_id "
                        + "WHERE orders.order_status = 'ORDER_COMPLETED' "
                        + "GROUP BY item_id, year, month")
                .rowMapper(new ColumnMapRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Map<String, Object>, MonthlyItemSalesStatistics> monthlyItemSalesProcessor() {
        return resultMap -> {
            Long itemId = (Long) resultMap.get("item_id");
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + itemId));

            int year = ((Number) resultMap.get("year")).intValue();
            int month = ((Number) resultMap.get("month")).intValue();
            int totalSalesCount = ((Number) resultMap.get("total_sales_count")).intValue();

            return MonthlyItemSalesStatistics.of(item, year, month, totalSalesCount);
        };
    }

    @Bean
    public JdbcBatchItemWriter<MonthlyItemSalesStatistics> monthlyItemSalesWriter() {
        return new JdbcBatchItemWriterBuilder<MonthlyItemSalesStatistics>()
                .dataSource(dataSource)
                .sql("INSERT INTO monthly_item_sales_statistics (item_id, year, month, total_sales_count) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE total_sales_count = VALUES(total_sales_count)")
                .itemPreparedStatementSetter(
                        new ItemPreparedStatementSetter<MonthlyItemSalesStatistics>() {
                            @Override
                            public void setValues(MonthlyItemSalesStatistics item,
                                    PreparedStatement ps)
                                    throws SQLException {
                                ps.setLong(1, item.getItem().getId());
                                ps.setInt(2, item.getYear());
                                ps.setInt(3, item.getMonth());
                                ps.setInt(4, item.getTotalSalesCount());
                            }
                        })
                .build();
    }
}

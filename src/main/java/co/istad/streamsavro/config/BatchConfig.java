package co.istad.streamsavro.config;

import co.istad.streamsavro.domain.Report1;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcCursorItemReader<Report1> reader(
            @Value("#{jobParameters['jobId']}") Long jobId,
            @Value("#{jobParameters['duration']}") Long duration
    ) {
        return new JdbcCursorItemReaderBuilder<Report1>()
                .name("report1")
                .dataSource(dataSource)
                .sql("SELECT * FROM employees.process_long_running_task(?, ?)")
                .preparedStatementSetter(ps -> {
                    ps.setLong(1, jobId);
                    ps.setLong(2, duration);
                })
                .rowMapper(new BeanPropertyRowMapper<>(Report1.class))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Report1> writer() {
        return new FlatFileItemWriterBuilder<Report1>()
                .name("report1")
                .resource(new FileSystemResource("/report"))
                .delimited()
                .names("process_long_running_task")
                .headerCallback(writer -> writer.write("process_long_running_task"))
                .build();
    }

    @Bean
    public Step exportStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("exportStep", jobRepository)
                .<Report1, Report1>chunk(100, transactionManager)
                .reader(reader(null, null))
                .writer(writer())
                .build();
    }

    @Bean
    public Job exportJob(JobRepository jobRepository, Step exportStep) {
        return new JobBuilder("exportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(exportStep)
                .end()
                .build();
    }


}

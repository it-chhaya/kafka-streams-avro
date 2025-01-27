package co.istad.streamsavro.config;

import co.istad.streamsavro.domain.Report2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
    public JdbcCursorItemReader<Report2> reader(
            @Value("#{jobParameters['depId']}") String depId
    ) {
        return new JdbcCursorItemReaderBuilder<Report2>()
                .name("report1")
                .dataSource(dataSource)
                .sql("SELECT * FROM employees.get_employee_report(?)")
                .preparedStatementSetter(ps -> {
                    ps.setString(1, depId);
                })
                .rowMapper(new BeanPropertyRowMapper<>(Report2.class))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Report2> writer() {
        return new FlatFileItemWriterBuilder<Report2>()
                .name("report1")
                .resource(new FileSystemResource("C:\\Users\\iLJiMae\\OneDrive\\Documents\\test-report\\report-1.csv"))
                .delimited()
                .names("employee_unique_id","employee_first_name","employee_last_name")
                .headerCallback(writer -> writer.write("employee_unique_id,employee_first_name,employee_last_name"))
                .build();
    }

    @Bean
    public Step exportStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("exportStep", jobRepository)
                .<Report2, Report2>chunk(100, transactionManager)
                .reader(reader(null))
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

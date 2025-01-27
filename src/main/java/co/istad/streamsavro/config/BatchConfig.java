package co.istad.streamsavro.config;

import co.istad.streamsavro.domain.EmployeeReport;
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

    // 1. Data Source Config
    private final DataSource dataSource;

    // 2. Execute Batch with Data Source
    @Bean
    @StepScope
    public JdbcCursorItemReader<EmployeeReport> reader(
            @Value("#{jobParameters['depId']}") String depId
    ) {
        return new JdbcCursorItemReaderBuilder<EmployeeReport>()
                .name("employee-report")
                .dataSource(dataSource)
                .sql("SELECT * FROM employees.get_employee_report(?)")
                .preparedStatementSetter(ps -> {
                    ps.setString(1, depId);
                })
                .rowMapper(new BeanPropertyRowMapper<>(EmployeeReport.class))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<EmployeeReport> writer() {
        return new FlatFileItemWriterBuilder<EmployeeReport>()
                .name("employee-report")
                .resource(new FileSystemResource("D:\\TEST-REPORT\\employee-reports.csv"))
                .delimited()
                .names("employee_unique_id","employee_first_name","employee_last_name", "employee_gender", "employee_dob", "department_name", "title_name", "current_salary", "updated_at")
                .headerCallback(writer -> writer.write("employee_unique_id,employee_first_name,employee_last_name,employee_gender,employee_dob,department_name,title_name,current_salary,updated_at"))
                .build();
    }

    @Bean
    public Step exportStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("employeeReportExportStep", jobRepository)
                .<EmployeeReport, EmployeeReport>chunk(100, transactionManager)
                .reader(reader(null))
                .writer(writer())
                .build();
    }

    @Bean
    public Job exportJob(JobRepository jobRepository, Step exportStep) {
        return new JobBuilder("employeeReportExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(exportStep)
                .end()
                .build();
    }



}

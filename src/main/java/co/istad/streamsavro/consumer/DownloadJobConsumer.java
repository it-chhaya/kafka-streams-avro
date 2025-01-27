package co.istad.streamsavro.consumer;

import co.istad.streamsavro.domain.DownloadJob;
import co.istad.streamsavro.domain.DownloadJobRepository;
import co.istad.streamsavro.domain.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DownloadJobConsumer {

    private final DownloadJobRepository downloadJobRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final JobLauncher jobLauncher;
    private final Job job;

    @KafkaListener(topics = "report1-request", groupId = "report1.download")
    public void report1Consumer(Long jobId) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        log.info("Consumer started - report 1");
        log.info("Consumer started - report 1 - job id {}", jobId);

        // Validate jobId
        DownloadJob downloadJob = downloadJobRepository
                .findById(jobId)
                .orElseThrow();

        // Start execute function or procedure
        /*downloadJobRepository.executeLongRunningProcess(downloadJob.getId(),
                180);*/
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("depId", "d005")
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        System.out.println("------------------------------");
        System.out.printf("%d\n%s\n%s",
                jobExecution.getJobId(),
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());

        downloadJob.setIsCompleted(true);
        downloadJob.setUpdatedAt(LocalDateTime.now());
        downloadJob.setResultPath("report1.csv");
        downloadJob.setStatus(JobStatus.COMPLETED);
        downloadJob.setProgress(100);

        downloadJob = downloadJobRepository.save(downloadJob);

        messagingTemplate.convertAndSend("/topic/report1-download", downloadJob);
    }

}

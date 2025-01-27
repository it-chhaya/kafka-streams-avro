package co.istad.streamsavro.consumer;

import co.istad.streamsavro.domain.DownloadJob;
import co.istad.streamsavro.domain.DownloadJobRepository;
import co.istad.streamsavro.domain.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

        DownloadJob downloadJob = downloadJobRepository
                .findById(jobId)
                .orElseThrow();

        log.info("Consumer started - report 1");
        log.info("Consumer started - jobId: {}", jobId);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("depId", "d005")
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        log.info("Consumer downloaded");
        System.out.println(jobExecution.getStatus() + " - " + jobExecution.getJobInstance().getJobName());

        downloadJob.setResultPath("/server-path/report1-1.csv");
        downloadJob.setStatus(JobStatus.COMPLETED);
        downloadJob.setUpdatedAt(LocalDateTime.now());
        downloadJobRepository.save(downloadJob);

        log.info("Consumer notified websocket");
        messagingTemplate.convertAndSend("/topic/report1-request", jobId);

    }

}

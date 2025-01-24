package co.istad.streamsavro.service;

import co.istad.streamsavro.domain.DownloadJob;
import co.istad.streamsavro.domain.DownloadJobRepository;
import co.istad.streamsavro.domain.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl {

    private final DownloadJobRepository downloadJobRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void initDownloadReport1(Principal principal) {
        DownloadJob job = new DownloadJob();
        job.setProcessName("REPORT_1_DOWNLOAD");
        job.setStatus(JobStatus.INITIALIZED);
        job.setUsername(principal.getName());
        job.setIsCompleted(false);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setProgress(0);

        job = downloadJobRepository.save(job);

        // Produce message to Kafka for download report
        // Asynchronous (Background)
        kafkaTemplate.send("report1-request", job.getId());
    }


}

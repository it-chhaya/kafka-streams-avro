package co.istad.streamsavro.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DownloadJobRepository extends JpaRepository<DownloadJob, Long> {

    @Query(value = "SELECT * FROM employees.process_long_running_task(:jobId, :duration)", nativeQuery = true)
    void executeDownloadJob(Long jobId, Integer duration);

}

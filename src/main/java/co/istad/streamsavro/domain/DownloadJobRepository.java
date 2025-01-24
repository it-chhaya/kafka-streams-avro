package co.istad.streamsavro.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DownloadJobRepository extends JpaRepository<DownloadJob, Long> {

    @Query(value = """
        SELECT *
        FROM employees.process_long_running_task(?1, ?2)
    """, nativeQuery=true)
    void executeLongRunningProcess(Long jobId, Integer duration);

}

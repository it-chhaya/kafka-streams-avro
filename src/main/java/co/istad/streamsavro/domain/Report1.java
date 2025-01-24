package co.istad.streamsavro.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Report1 {
    @Id
    private Long id;
    private String process_long_running_task;
}

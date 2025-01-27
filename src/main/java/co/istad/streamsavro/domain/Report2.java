package co.istad.streamsavro.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Report2 {
    private Long employee_unique_id;
    private String employee_first_name;
    private String employee_last_name;
}

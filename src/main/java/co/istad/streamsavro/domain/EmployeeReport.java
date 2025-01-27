package co.istad.streamsavro.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EmployeeReport {

    private Long employee_unique_id;
    private String employee_first_name;
    private String employee_last_name;
    private String employee_gender;
    private LocalDate employee_dob;
    private String department_name;
    private String title_name;
    private Long current_salary;
    private LocalDateTime updated_at;

}

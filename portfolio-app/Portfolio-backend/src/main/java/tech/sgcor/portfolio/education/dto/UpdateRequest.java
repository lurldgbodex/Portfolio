package tech.sgcor.portfolio.education.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateRequest {
    private Long id;
    private String school;
    private String degree;
    private String course;
    private String grade;
    private String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String start_date;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String end_date;
}

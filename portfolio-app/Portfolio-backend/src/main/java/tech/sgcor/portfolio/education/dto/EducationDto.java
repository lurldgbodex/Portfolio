package tech.sgcor.portfolio.education.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationDto {
    private Long id;
    @NotNull(message = "user_id is required")
    private Long user_id;
    @NotBlank(message = "school is required")
    private String school;
    @NotBlank(message = "degree is required")
    private String degree;
    @NotBlank(message = "course is required")
    private String course;
    @NotBlank(message = "grade is required")
    private String grade;
    @NotBlank(message = "description is required")
    private String description;
    @NotNull(message = "start_date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String start_date;
    @NotNull(message = "end_date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String end_date;
}

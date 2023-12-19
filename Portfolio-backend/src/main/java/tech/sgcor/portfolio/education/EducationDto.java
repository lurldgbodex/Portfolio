package tech.sgcor.portfolio.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EducationDto {
    private Long id;
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
    private LocalDate start_date;
    @NotNull(message = "end_date is required")
    private LocalDate end_date;
}

package tech.sgcor.portfolio.experience.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ExperienceDto {
    private Long id;
    @NotNull(message = "user_id is required")
    private Long user_id;
    @NotBlank(message = "company cannot be empty or null")
    private String company;
    @NotBlank(message = "role cannot be empty or null")
    private String role;
    @NotBlank(message = "description cannot be empty or null")
    private String description;
    @NotNull(message = "start_date cannot be null")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String start_date;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String end_date;
}

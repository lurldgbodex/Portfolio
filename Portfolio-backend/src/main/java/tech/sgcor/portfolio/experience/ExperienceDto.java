package tech.sgcor.portfolio.experience;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import tech.sgcor.portfolio.shared.ValidLocalDate;

import java.time.LocalDate;

@Data
@Builder
public class ExperienceDto {
    private Long id;
    @NotBlank(message = "company cannot be empty or null")
    private String company;
    @NotBlank(message = "role cannot be empty or null")
    private String role;
    @NotBlank(message = "description cannot be empty or null")
    private String description;
    @NotNull(message = "start_date cannot be null")
    @ValidLocalDate
    private LocalDate start_date;
    @ValidLocalDate
    private LocalDate end_date;
}

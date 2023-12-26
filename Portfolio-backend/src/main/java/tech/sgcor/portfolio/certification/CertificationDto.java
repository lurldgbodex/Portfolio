package tech.sgcor.portfolio.certification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tech.sgcor.portfolio.validation.ValidLocalDate;

import java.time.LocalDate;
import java.util.List;

@Data
public class CertificationDto {
    @NotNull(message = "user_id is required")
    private Long user_id;
    @NotBlank(message = "name is required and should not be blank")
    private String name;
    @NotBlank(message = "body is required and should not be blank")
    private String body;
    @ValidLocalDate
    @NotNull(message = "date is required and should not be null")
    private LocalDate date;
    @NotNull(message = "details is required and should be a list")
    private List<String> details;
}

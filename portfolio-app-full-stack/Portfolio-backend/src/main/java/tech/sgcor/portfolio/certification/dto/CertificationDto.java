package tech.sgcor.portfolio.certification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import tech.sgcor.portfolio.validation.IsValidList;

import java.util.List;

@Data
public class CertificationDto {
    @NotNull(message = "user_id is required")
    private Long user_id;
    @NotBlank(message = "name is required and should not be blank")
    private String name;
    @NotBlank(message = "body is required and should not be blank")
    private String body;
    @NotNull(message = "date is required and should not be null")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String date;
    @IsValidList(message = "details na list e dey expect and e no fit empty")
    private List<String> details;
}

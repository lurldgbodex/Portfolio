package tech.sgcor.portfolio.certification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDetailRequest {
    @NotBlank(message = "detail is required and should not be blank")
    private String detail;
}

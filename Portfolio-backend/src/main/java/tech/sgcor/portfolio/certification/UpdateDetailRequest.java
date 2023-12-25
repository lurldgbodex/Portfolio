package tech.sgcor.portfolio.certification;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDetailRequest {
    @NotBlank(message = "detail is required and should not be blank")
    private String detail;
}

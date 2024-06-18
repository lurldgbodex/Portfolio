package tech.sgcor.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetRequest {
    @NotBlank(message = "you need to provide your email")
    @Email(message = "invalid email provided")
    private String email;
}

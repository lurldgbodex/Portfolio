package tech.sgcor.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserDetails {
    private String first_name;
    private String last_name;
    private String other_name;
    @NotBlank(message = "email is required")
    private String email;
}

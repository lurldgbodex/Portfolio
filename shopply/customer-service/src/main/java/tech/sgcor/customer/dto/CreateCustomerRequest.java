package tech.sgcor.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "first_name is required")
    private String first_name;
    @NotBlank(message = "last_name is required")
    private String last_name;
    private String other_name;
    @NotBlank(message = "email is required")
    @Email(message = "invalid email provided")
    private String email;
    @NotBlank(message = "password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]{};':\"\\|,.<>\\/\\?]).{8,}$", message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;
}

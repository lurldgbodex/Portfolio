package tech.sgcor.portfolio.about;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CreateRequest {
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Address is required")
    private String address;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "provide dob in pattern 'yyyy-mm-dd")
    private LocalDate dob;
    @NotNull(message = "title is required")
    private String title;
    @Pattern(regexp = "^\\+\\d{14}$", message = "Provide a valid phone number")
    private String phone_number;
    @NotNull(message = "Summary is required")
    private String summary;
    @Email(message = "Provide a valid email address")
    private String email;
    @NotNull(message = "Github url is required")
    private String github;
    @NotNull(message = "Linkedin profile url is required")
    private String linkedin;
    private String medium;
}

package tech.sgcor.portfolio.about;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import tech.sgcor.portfolio.validation.ValidLocalDate;


import java.time.LocalDate;

@Data
public class CreateRequest {
    @NotBlank(message = "first_name is required")
    private String first_name;

    @NotBlank(message = "last_name is required")
    private String last_name;

    private String middle_name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Date of Birth (dob) is required")
    @ValidLocalDate
    private LocalDate dob;

    @NotBlank(message = "title is required")
    private String title;

    @Pattern(regexp = "^\\+\\d{11,15}$", message = "numbers between 11 and 15 character starting with +")
    @NotBlank(message = "phoneNumber is required")
    private String phone_number;

    @NotBlank(message = "Summary is required")
    private String summary;

    @Email(message = "Provide a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Github url is required")
    private String github;

    @NotBlank(message = "Linkedin profile url is required")
    private String linkedin;

    private String medium;
}

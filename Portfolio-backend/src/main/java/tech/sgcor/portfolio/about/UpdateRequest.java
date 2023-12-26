package tech.sgcor.portfolio.about;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import tech.sgcor.portfolio.validation.ValidLocalDate;

import java.time.LocalDate;

@Data
public class UpdateRequest {
    private String first_name;
    private String last_name;
    private String middle_name;
    private String address;

    @ValidLocalDate
    private LocalDate dob;

    private String title;

    @Pattern(regexp = "^\\+\\d{11,15}$", message = "numbers between 11 and 15 character starting with +")
    private String phone_number;

    private String summary;

    @Email(message = "Please provide a valid email")
    private String email;

    private String github;
    private String linkedin;
    private String medium;
}

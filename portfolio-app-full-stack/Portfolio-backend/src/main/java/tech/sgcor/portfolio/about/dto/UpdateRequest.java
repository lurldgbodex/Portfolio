package tech.sgcor.portfolio.about.dto;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateRequest {
    private String first_name;
    private String last_name;
    private String middle_name;
    private String address;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "na the patter for valid date be this 'yyyy-MM-dd'.")
    private String dob;

    private String title;

    @Pattern(regexp = "^\\+\\d{11,15}$", message = "valid number na between 11 and 15 character. start with ur country code. e.g +234")
    private String phone_number;

    private String summary;

    @Email(message = "na only valid email we dey accept")
    private String email;

    private String github;
    private String linkedin;
    private String medium;
    private String twitter;
    private String image_url;

    private String cv;
}

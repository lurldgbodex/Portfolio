package tech.sgcor.portfolio.about.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateRequest {
    @NotBlank(message = "una no get first_name?")
    private String first_name;

    @NotBlank(message = "Una no get last_name?")
    private String last_name;

    private String middle_name;

    @NotBlank(message = "una no get address ba?")
    private String address;

    @NotNull(message = "una drop from heaven ba. oga provide una date of birth(dob) jare.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "dob na for this pattern 'yyyy-MM-dd' we dey accept.")
    private String dob;

    @NotBlank(message = "make una provide ur title na")
    private String title;

    @Pattern(regexp = "^\\+\\d{11,15}$", message = "valid number na between 11 and 15 character. start with ur country code. e.g +234")
    @NotBlank(message = "na only adult fit access if una no get phone")
    private String phone_number;

    @NotBlank(message = "make una provide short bio as summary")
    private String summary;

    @Email(message = "na only valid email we dey accept")
    @NotBlank(message = "una no get email abi")
    private String email;

    @NotBlank(message = "shey u no be developer ni. how u no get github?")
    private String github;

    @NotBlank(message = "how u no go get linkedin for this generation")
    private String linkedin;

    @NotBlank(message = "make una provide una profile image_url")
    private String image_url;

    @NotBlank(message = "upload una cv url")
    private String cv;

    private String medium;

    private String twitter;
}

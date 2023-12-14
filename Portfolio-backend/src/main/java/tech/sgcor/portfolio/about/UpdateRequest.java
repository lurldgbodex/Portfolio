package tech.sgcor.portfolio.about;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateRequest {
    private String name;
    private String address;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;
    private String title;
    private String phone_number;
    private String summary;
    private String email;
    private String github;
    private String linkedin;
    private String medium;
}

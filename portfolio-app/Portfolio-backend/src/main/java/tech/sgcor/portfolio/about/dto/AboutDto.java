package tech.sgcor.portfolio.about.dto;

import lombok.Builder;
import lombok.Data;
import tech.sgcor.portfolio.user.entity.User;

import java.time.LocalDate;

@Data
@Builder
public class AboutDto {
    private Long id;
    private String address;
    private LocalDate dob;
    private String title;
    private String phoneNumber;
    private String summary;
    private String email;
    private String github;
    private String linkedin;
    private String medium;
    private String twitter;
    private String cv;
    private User user;
}

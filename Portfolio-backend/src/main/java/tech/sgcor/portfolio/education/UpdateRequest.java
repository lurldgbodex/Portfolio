package tech.sgcor.portfolio.education;

import lombok.Data;
import tech.sgcor.portfolio.shared.ValidLocalDate;

import java.time.LocalDate;

@Data
public class UpdateRequest {
    private Long id;
    private String school;
    private String degree;
    private String course;
    private String grade;
    private String description;
    @ValidLocalDate
    private LocalDate start_date;
    @ValidLocalDate
    private LocalDate end_date;
}

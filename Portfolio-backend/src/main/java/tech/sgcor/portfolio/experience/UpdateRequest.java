package tech.sgcor.portfolio.experience;

import lombok.Data;
import tech.sgcor.portfolio.shared.ValidLocalDate;

import java.time.LocalDate;

@Data
public class UpdateRequest {
    private String company;
    private String role;
    private String description;
    @ValidLocalDate
    private LocalDate start_date;
    @ValidLocalDate
    private LocalDate end_date;
}

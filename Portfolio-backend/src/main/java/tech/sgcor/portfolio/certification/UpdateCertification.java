package tech.sgcor.portfolio.certification;

import lombok.Data;
import tech.sgcor.portfolio.validation.ValidLocalDate;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateCertification {
    private String name;
    private String body;
    @ValidLocalDate
    private LocalDate date;
    private List<String> details;
}

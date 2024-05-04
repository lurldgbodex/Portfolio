package tech.sgcor.portfolio.certification;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCertification {
    private String name;
    private String body;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.")
    private String date;
    private List<String> details;
}

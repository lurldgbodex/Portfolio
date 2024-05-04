package tech.sgcor.portfolio.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectDetailsUpdate {
    @NotBlank(message = "You need to provide the field you want to update")
    private String detail;
}

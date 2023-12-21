package tech.sgcor.portfolio.skills;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SkillRequest {
    @NotBlank(message = "name is required and cannot be blank")
    private String name;
    @NotNull(message = "skill is required and cannot be blank")
    private List<String> skill;
}

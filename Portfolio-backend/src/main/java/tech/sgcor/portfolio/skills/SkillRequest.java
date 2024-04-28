package tech.sgcor.portfolio.skills;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tech.sgcor.portfolio.validation.IsValidList;

import java.util.List;

@Data
public class SkillRequest {
    @NotBlank(message = "name is required and cannot be blank")
    private String name;
    @IsValidList(message = "oga provide a list of skill")
    private List<String> skill;
    @NotNull(message = "user_id is required")
    private Long user_id;
}

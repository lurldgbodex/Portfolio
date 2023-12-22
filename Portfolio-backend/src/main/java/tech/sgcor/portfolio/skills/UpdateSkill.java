package tech.sgcor.portfolio.skills;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSkill {
    @NotBlank(message = "skill field cannot be blank")
    public String skill;
}

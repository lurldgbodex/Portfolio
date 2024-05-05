package tech.sgcor.portfolio.skills.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSkill {
    @NotBlank(message = "skill field cannot be blank")
    public String skill;
}

package tech.sgcor.portfolio.skills.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkillUpdateRequest {
    private String name;
    private List<String> skill;
}

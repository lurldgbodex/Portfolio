package tech.sgcor.portfolio.skills.dto;

import tech.sgcor.portfolio.skills.entity.SkillType;

import java.util.List;

public record GetSkill(List<SkillType> skills) {
}

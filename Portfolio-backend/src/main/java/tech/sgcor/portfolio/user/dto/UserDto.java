package tech.sgcor.portfolio.user;

import lombok.Builder;
import lombok.Data;
import tech.sgcor.portfolio.about.entity.About;
import tech.sgcor.portfolio.certification.entity.Certification;
import tech.sgcor.portfolio.education.entity.Education;
import tech.sgcor.portfolio.experience.entity.Experience;
import tech.sgcor.portfolio.language.entity.Language;
import tech.sgcor.portfolio.project.entity.Project;
import tech.sgcor.portfolio.skills.SkillType;

import java.util.List;

@Data
@Builder
public class UserDto {
    private About about;
    private List<Certification> certifications;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Language> languages;
    private List<Project> projects;
    private List<SkillType> skills;
}

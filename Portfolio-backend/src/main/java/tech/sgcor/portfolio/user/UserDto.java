package tech.sgcor.portfolio.user;

import lombok.Builder;
import lombok.Data;
import tech.sgcor.portfolio.about.About;
import tech.sgcor.portfolio.certification.Certification;
import tech.sgcor.portfolio.education.Education;
import tech.sgcor.portfolio.experience.Experience;
import tech.sgcor.portfolio.language.Language;
import tech.sgcor.portfolio.project.Project;
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

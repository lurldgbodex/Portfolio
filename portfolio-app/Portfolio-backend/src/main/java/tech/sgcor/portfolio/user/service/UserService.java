package tech.sgcor.portfolio.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.about.dto.AboutDto;

import tech.sgcor.portfolio.about.service.AboutService;
import tech.sgcor.portfolio.certification.entity.Certification;
import tech.sgcor.portfolio.certification.service.CertificationService;
import tech.sgcor.portfolio.education.entity.Education;
import tech.sgcor.portfolio.education.service.EducationService;
import tech.sgcor.portfolio.experience.entity.Experience;
import tech.sgcor.portfolio.experience.service.ExperienceService;
import tech.sgcor.portfolio.language.entity.Language;
import tech.sgcor.portfolio.language.service.LanguageService;
import tech.sgcor.portfolio.project.entity.Project;
import tech.sgcor.portfolio.project.service.ProjectService;
import tech.sgcor.portfolio.skills.service.SkillService;
import tech.sgcor.portfolio.skills.entity.SkillType;
import tech.sgcor.portfolio.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AboutService aboutService;
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final ProjectService projectService;
    private final SkillService skillService;
    private final LanguageService languageService;
    private final CertificationService certificationService;

    public UserDto getUSerData(Long userId) {
        AboutDto about = aboutService.getAbout(userId);
        List<Experience> experiences = experienceService.getExperiences(userId);
        List<Education> educations = educationService.getEducations(userId);
        List<Project> projects = projectService.getProjects(userId);
        List<SkillType> skills = skillService.getSkills(userId);
        List<Language> languages = languageService.getLanguages(userId);
        List<Certification> certifications = certificationService.getCertifications(userId);

        return UserDto
                .builder()
                .about(about)
                .experiences(experiences)
                .educations(educations)
                .projects(projects)
                .skills(skills)
                .languages(languages)
                .certifications(certifications)
                .build();
    }
}
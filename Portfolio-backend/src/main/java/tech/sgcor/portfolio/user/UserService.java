package tech.sgcor.portfolio.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.about.About;
import tech.sgcor.portfolio.about.AboutService;
import tech.sgcor.portfolio.certification.Certification;
import tech.sgcor.portfolio.certification.CertificationService;
import tech.sgcor.portfolio.education.Education;
import tech.sgcor.portfolio.education.EducationService;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.experience.Experience;
import tech.sgcor.portfolio.experience.ExperienceService;
import tech.sgcor.portfolio.language.Language;
import tech.sgcor.portfolio.language.LanguageService;
import tech.sgcor.portfolio.project.Project;
import tech.sgcor.portfolio.project.ProjectService;
import tech.sgcor.portfolio.skills.SkillService;
import tech.sgcor.portfolio.skills.SkillType;

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
        About about = aboutService.getAbout(userId);
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

package tech.sgcor.portfolio.experience.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.experience.dto.ExperienceDto;
import tech.sgcor.portfolio.experience.dto.UpdateRequest;
import tech.sgcor.portfolio.experience.entity.Experience;
import tech.sgcor.portfolio.experience.repository.ExperienceRepository;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceRepository experienceRepository;

    public List<Experience> getExperiences(Long userId) {
        return experienceRepository.findByUserId(userId);
    }

    public Experience addExperience(ExperienceDto request) {
        Experience experience = new Experience();
        experience.setUserId(request.getUser_id());
        experience.setCompany(request.getCompany());
        experience.setRole(request.getRole());
        experience.setDescription(request.getDescription());
        experience.setStartDate(LocalDate.parse(request.getStart_date()));
        experience.setEndDate(LocalDate.parse(request.getEnd_date()));

        return experienceRepository.save(experience);
    }

    public CustomResponse update(
            Long id, UpdateRequest request) {
        var experience = experienceRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getCompany(), ""),
                Objects.toString(request.getRole(), ""),
                Objects.toString(request.getDescription(), ""),
                Objects.toString(request.getStart_date(), ""),
                Objects.toString(request.getEnd_date(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("At least one field must be non-blank to perform the update");
        }

        experience.setCompany(SharedService.isNotBlank(request.getCompany()) ? request.getCompany() : experience.getCompany());
        experience.setRole(SharedService.isNotBlank(request.getRole()) ? request.getRole() : experience.getRole());
        experience.setDescription(SharedService.isNotBlank(request.getDescription()) ? request.getDescription() : experience.getDescription());
        experience.setStartDate(request.getStart_date() != null ? LocalDate.parse(request.getStart_date()) : experience.getStartDate());
        experience.setEndDate(request.getEnd_date() != null ? LocalDate.parse(request.getEnd_date()) : experience.getEndDate());

        experienceRepository.save(experience);

        return new CustomResponse(HttpStatus.OK.value(),
                "Resource updated successfully", HttpStatus.OK);
    }

    public CustomResponse delete(Long id) {
        var experience = experienceRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));
        experienceRepository.deleteById(experience.getId());
        
        return new CustomResponse(HttpStatus.OK.value(),
                "Resource deleted successfully", HttpStatus.OK);
    }
}

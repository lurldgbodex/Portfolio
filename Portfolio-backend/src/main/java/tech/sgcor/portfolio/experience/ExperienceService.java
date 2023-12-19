package tech.sgcor.portfolio.experience;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tech.sgcor.portfolio.about.AboutService;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.util.Objects;
import java.util.stream.Stream;


@Service
@Validated
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceRepository experienceRepository;

    public ExperienceDto get(Long id) throws ResourceNotFound {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Resource not found with id"));

        return ExperienceDto
                .builder()
                .id(experience.getId())
                .company(experience.getCompany())
                .role(experience.getRole())
                .description(experience.getDescription())
                .start_date(experience.getStartDate())
                .end_date(experience.getEndDate())
                .build();
    }

    public Experience addExperience(@Valid ExperienceDto request) {
        Experience experience = new Experience();
        experience.setCompany(request.getCompany());
        experience.setRole(request.getRole());
        experience.setDescription(request.getDescription());
        experience.setStartDate(request.getStart_date());
        experience.setEndDate(request.getEnd_date());

        return experienceRepository.save(experience);
    }

    public CustomResponse update(
            Long id, @Valid  UpdateRequest request) throws ResourceNotFound, BadRequestException {
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

        experience.setCompany(AboutService.isNotBlank(request.getCompany()) ? request.getCompany() : experience.getCompany());
        experience.setRole(AboutService.isNotBlank(request.getRole()) ? request.getRole() : experience.getRole());
        experience.setDescription(AboutService.isNotBlank(request.getDescription()) ? request.getDescription() : experience.getDescription());
        experience.setStartDate(request.getStart_date() != null ? request.getStart_date() : experience.getStartDate());
        experience.setEndDate(request.getEnd_date() != null ? request.getEnd_date() : experience.getEndDate());

        experienceRepository.save(experience);

        return new CustomResponse(HttpStatus.OK.value(),
                "Resource updated successfully", HttpStatus.OK);
    }

    public CustomResponse delete(Long id) throws ResourceNotFound {
        var experience = experienceRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));
        experienceRepository.deleteById(experience.getId());
        
        return new CustomResponse(HttpStatus.OK.value(),
                "Resource deleted successfully", HttpStatus.OK);
    }
}

package tech.sgcor.portfolio.education;

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
public class EducationService {
    private final EducationRepository repository;

    public EducationDto getEducation(long id) throws ResourceNotFound {
        var education = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        return EducationDto
                .builder()
                .id(education.getId())
                .school(education.getSchool())
                .degree(education.getDegree())
                .course(education.getCourse())
                .description(education.getDescription())
                .grade(education.getGrade())
                .start_date(education.getStartDate())
                .end_date(education.getEndDate())
                .build();
    }

    public Education createEducation(@Valid EducationDto request) {
        Education education = new Education();

        education.setSchool(request.getSchool());
        education.setDegree(request.getDegree());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());
        education.setCourse(request.getCourse());
        education.setStartDate(request.getStart_date());
        education.setEndDate(request.getEnd_date());

        return repository.save(education);
    }

    public Education updateEducation(
            Long id, @Valid UpdateRequest request) throws ResourceNotFound, BadRequestException {
        var education = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getSchool(), ""),
                Objects.toString(request.getDegree(), ""),
                Objects.toString(request.getGrade(), ""),
                Objects.toString(request.getDescription(), ""),
                Objects.toString(request.getStart_date(), ""),
                Objects.toString(request.getCourse(), ""),
                Objects.toString(request.getEnd_date(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("At least one field must not be blank to perform update");
        }

        education.setSchool(AboutService.isNotBlank(request.getSchool()) ? request.getSchool() : education.getSchool());
        education.setDegree(AboutService.isNotBlank(request.getDegree()) ? request.getDegree() : education.getDegree());
        education.setGrade(AboutService.isNotBlank(request.getGrade()) ? request.getGrade() : education.getGrade());
        education.setDescription(AboutService.isNotBlank(request.getDescription()) ? request.getDescription() : education.getDescription());
        education.setStartDate(request.getStart_date() != null ? request.getStart_date() : education.getStartDate());
        education.setEndDate(request.getEnd_date() != null ? request.getEnd_date() : education.getEndDate());
        education.setCourse(AboutService.isNotBlank(request.getCourse()) ? request.getCourse() : education.getCourse());

        return repository.save(education);
    }

    public CustomResponse deleteEducation(Long id) throws ResourceNotFound {
        var education = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Resource not found with id"));

        repository.delete(education);
        return new CustomResponse(
                HttpStatus.OK.value(),
                "Resource deleted successfully",
                HttpStatus.OK);
    }
}

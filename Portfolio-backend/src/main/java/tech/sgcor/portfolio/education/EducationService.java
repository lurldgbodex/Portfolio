package tech.sgcor.portfolio.education;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository repository;

    public List<Education> getEducations(Long userId) {
        return repository.findByUserId(userId);
    }

    public Education createEducation(EducationDto request) {
        Education education = new Education();

        education.setSchool(request.getSchool());
        education.setUserId(request.getUser_id());
        education.setDegree(request.getDegree());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());
        education.setCourse(request.getCourse());
        education.setStartDate(LocalDate.parse(request.getStart_date()));
        education.setEndDate(LocalDate.parse(request.getEnd_date()));

        return repository.save(education);
    }

    public Education updateEducation(
            Long id, UpdateRequest request) {
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

        education.setSchool(SharedService.isNotBlank(request.getSchool())
                ? request.getSchool() : education.getSchool());
        education.setDegree(SharedService.isNotBlank(request.getDegree())
                ? request.getDegree() : education.getDegree());
        education.setGrade(SharedService.isNotBlank(request.getGrade())
                ? request.getGrade() : education.getGrade());
        education.setDescription(SharedService.isNotBlank(request.getDescription())
                ? request.getDescription() : education.getDescription());
        education.setStartDate(request.getStart_date() != null
                ? LocalDate.parse(request.getStart_date()) : education.getStartDate());
        education.setEndDate(request.getEnd_date() != null
                ? LocalDate.parse(request.getEnd_date()) : education.getEndDate());
        education.setCourse(SharedService.isNotBlank(request.getCourse())
                ? request.getCourse() : education.getCourse());

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

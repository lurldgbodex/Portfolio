package tech.sgcor.portfolio.project;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Validated
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectDetailsRepository projectDetailsRepository;

    @Transactional
    public Project createProject(@Valid ProjectDto request) {
        Project project = new Project();

        project.setName(request.getName());
        project.setType(request.getType());
        project.setUrl(request.getUrl());
        project.setUserId(request.getUser_id());

        List<ProjectDetails> details = request.getDetails()
                .stream()
                .map((val) -> {
                    ProjectDetails detail = new ProjectDetails();
                    detail.setDetails(val);
                    detail.setProject(project);
                    return detail;
                })
                .toList();
        project.setDetails(details);
        return projectRepository.save(project);
    }

    public List<Project> getProjects(Long userId) {
        return projectRepository.findByUserId(userId);
    }

    @Transactional
    public CustomResponse updateProject(
            long id, ProjectUpdate request) throws ResourceNotFound, BadRequestException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Project not found with id"));

        boolean hasNonBlankField = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getType(), ""),
                Objects.toString(request.getUrl(), ""),
                Objects.toString(request.getDetails(), "")
        ).anyMatch(field -> !StringUtils.isBlank(field));

        if (!hasNonBlankField) {
            throw new BadRequestException("you need to provide a field to update");
        }

        project.setName(SharedService.isNotBlank(request.getName())
                ? request.getName() : project.getName());
        project.setType(SharedService.isNotBlank(request.getType())
                ? request.getType() : project.getType());
        project.setUrl(SharedService.isNotBlank(request.getUrl())
                ? request.getUrl() : project.getUrl());

        projectRepository.save(project);

        List<ProjectDetails> details = project.getDetails();

        if (request.getDetails() != null) {
            int noOfDetails = Math.min(details.size(), request.getDetails().size());

            for (int i = 0; i < noOfDetails; i++) {
                ProjectDetails detail = details.get(i);
                var update = request.getDetails().get(i);
                detail.setDetails(SharedService.isNotBlank(update)
                        ? update : detail.getDetails());
            }

            if (request.getDetails().size() > noOfDetails) {
                for (int i = noOfDetails; i < request.getDetails().size(); i++) {
                    ProjectDetails newDetail = new ProjectDetails();
                    newDetail.setDetails(request.getDetails().get(i));
                    newDetail.setProject(project);
                    details.add(newDetail);
                }
            }
            projectDetailsRepository.saveAll(details);
        }

        return new CustomResponse(200, "Project updated successfully", HttpStatus.OK);
    }

    public CustomResponse updateDetails(
            long id, @Valid ProjectDetailsUpdate request) throws ResourceNotFound {
        ProjectDetails detail = projectDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Project detail not found with id"));

        detail.setDetails(request.getDetail());

        projectDetailsRepository.save(detail);

        return new CustomResponse(200, "project detail updated successfully", HttpStatus.OK);
    }

    @Transactional
    public CustomResponse deleteProject(long id) throws ResourceNotFound {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Project not found with id"));
        projectRepository.delete(project);

        return new CustomResponse(200, "Project deleted successfully", HttpStatus.OK);
    }
}

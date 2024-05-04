package tech.sgcor.portfolio.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.project.dto.ProjectDetailsUpdate;
import tech.sgcor.portfolio.project.dto.ProjectDto;
import tech.sgcor.portfolio.project.service.ProjectService;
import tech.sgcor.portfolio.project.dto.ProjectUpdate;
import tech.sgcor.portfolio.project.entity.Project;
import tech.sgcor.portfolio.shared.CustomResponse;

@RestController
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admins/projects")
public class ProjectController {
    private final ProjectService service;

    @PostMapping("/add")
    public ResponseEntity<Project> addProject(
            @RequestBody @Valid ProjectDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createProject(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateProject(
            @PathVariable Long id, @RequestBody @Valid ProjectUpdate request){
        return ResponseEntity.ok(service.updateProject(id, request));
    }

    @PatchMapping("/details/{id}")
    public ResponseEntity<CustomResponse> updateDetail(
            @PathVariable Long id, @RequestBody @Valid ProjectDetailsUpdate request) {
        return ResponseEntity.ok(service.updateDetails(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteProject(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteProject(id));
    }
}

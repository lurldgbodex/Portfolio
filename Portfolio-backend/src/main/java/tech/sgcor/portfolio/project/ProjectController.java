package tech.sgcor.portfolio.project;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;

    @GetMapping("{id}")
    public ResponseEntity<GetProject> getProject(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.getProject(id));
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> addProject(
            @RequestBody ProjectDto request, UriComponentsBuilder ucb) {
        Project project= service.createProject(request);
        URI location = ucb.path("/api/project/{id}").buildAndExpand(project.getId()).toUri();
        var res = new CustomResponse(201,
                "project created successfully", HttpStatus.CREATED);
        return ResponseEntity.created(location).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateProject(
            @PathVariable Long id, @RequestBody ProjectUpdate request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateProject(id, request));
    }

    @PatchMapping("/details/{id}")
    public ResponseEntity<CustomResponse> updateDetail(
            @PathVariable Long id, @RequestBody ProjectDetailsUpdate request) throws ResourceNotFound {
        return ResponseEntity.ok(service.updateDetails(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteProject(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteProject(id));
    }
}

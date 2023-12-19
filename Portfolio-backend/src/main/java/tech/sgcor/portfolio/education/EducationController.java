package tech.sgcor.portfolio.education;

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
@RequestMapping("/api/education")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService service;

    @GetMapping("/{id}")
    public ResponseEntity<EducationDto> getEducation(
            @PathVariable long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.getEducation(id));
    }

    @PostMapping("/create")
    public ResponseEntity<CustomResponse> createEducation(
            @RequestBody EducationDto request, UriComponentsBuilder ucb) {
        var education = service.createEducation(request);

        URI location = ucb.path("/api/education/{id}").buildAndExpand(education.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new CustomResponse(HttpStatus.CREATED.value(),
                        "Resource created successfully",
                        HttpStatus.CREATED)
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Education> updateEducation(
            @PathVariable Long id, @RequestBody UpdateRequest request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateEducation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteEducation(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteEducation(id));
    }
}

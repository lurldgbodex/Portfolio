package tech.sgcor.portfolio.education;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.net.URI;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService service;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> createEducation(
            @RequestBody EducationDto request, UriComponentsBuilder ucb) {
        var education = service.createEducation(request);

        URI location = ucb.path(SharedService.BASE_URL + "{id}")
                .buildAndExpand(education.getId()).toUri();

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

package tech.sgcor.portfolio.experience;

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
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {
    private final ExperienceService service;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> postExperience(
            @RequestBody ExperienceDto request, UriComponentsBuilder ucb) {
        var experience = service.addExperience(request);

        URI location = ucb.path(SharedService.BASE_URL + "{id}")
                .buildAndExpand(experience.getId()).toUri();

        return ResponseEntity
                .created(location)
                .body(new CustomResponse(HttpStatus.CREATED.value(),
                        "new resource created successfully",
                        HttpStatus.CREATED)
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> updateExperience(@PathVariable Long id,
            @RequestBody UpdateRequest request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteExperience(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.delete(id));
    }
}

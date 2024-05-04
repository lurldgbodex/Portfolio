package tech.sgcor.portfolio.experience.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.experience.dto.ExperienceDto;
import tech.sgcor.portfolio.experience.service.ExperienceService;
import tech.sgcor.portfolio.experience.dto.UpdateRequest;
import tech.sgcor.portfolio.experience.entity.Experience;
import tech.sgcor.portfolio.shared.CustomResponse;

@RestController
@RequestMapping("/api/admins/experiences")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class ExperienceController {
    private final ExperienceService service;

    @PostMapping("/add")
    public ResponseEntity<Experience> postExperience(
            @RequestBody @Valid ExperienceDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addExperience(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> updateExperience(@PathVariable Long id,
            @RequestBody @Valid UpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteExperience(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

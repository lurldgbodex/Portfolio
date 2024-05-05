package tech.sgcor.portfolio.education.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.education.entity.Education;
import tech.sgcor.portfolio.education.dto.EducationDto;
import tech.sgcor.portfolio.education.service.EducationService;
import tech.sgcor.portfolio.education.dto.UpdateRequest;
import tech.sgcor.portfolio.shared.CustomResponse;

@RestController
@RequestMapping("/api/admins/educations")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService service;

    @PostMapping("/add")
    public ResponseEntity<Education> createEducation(
            @RequestBody @Valid EducationDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createEducation(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Education> updateEducation(
            @PathVariable Long id, @RequestBody @Valid UpdateRequest request) {
        return ResponseEntity.ok(service.updateEducation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteEducation(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.deleteEducation(id));
    }
}

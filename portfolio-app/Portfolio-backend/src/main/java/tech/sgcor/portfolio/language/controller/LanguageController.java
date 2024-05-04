package tech.sgcor.portfolio.language.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.language.dto.LanguageDto;
import tech.sgcor.portfolio.language.service.LanguageService;
import tech.sgcor.portfolio.language.dto.LanguageUpdate;
import tech.sgcor.portfolio.language.entity.Language;
import tech.sgcor.portfolio.shared.CustomResponse;

@RestController
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admins/languages")
public class LanguageController {
    private final LanguageService service;

    @PostMapping("/add")
    public ResponseEntity<Language> createLanguage(
            @RequestBody @Valid LanguageDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createLanguage(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> updateLanguage(
            @PathVariable long id, @RequestBody @Valid LanguageUpdate request) {
        return ResponseEntity.ok(service.updateLanguage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteLanguage(@PathVariable long id) {
        return ResponseEntity.ok(service.deleteLanguage(id));
    }
}

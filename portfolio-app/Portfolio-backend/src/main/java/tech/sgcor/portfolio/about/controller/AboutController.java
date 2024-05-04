package tech.sgcor.portfolio.about.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.about.service.AboutService;
import tech.sgcor.portfolio.about.dto.CreateRequest;
import tech.sgcor.portfolio.about.dto.UpdateRequest;
import tech.sgcor.portfolio.about.entity.About;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.io.IOException;

@RestController
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
@RequestMapping("/api/admins/abouts")
public class AboutController {
    private final AboutService aboutService;

    @PostMapping("/add")
    private ResponseEntity<About> addAbout(@RequestBody @Valid CreateRequest request) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aboutService.add(request));
    }

    @PatchMapping("/{id}")
    private ResponseEntity<CustomResponse> updateAbout(
            @PathVariable Long id, @RequestBody @Valid UpdateRequest request) throws IOException {
        return ResponseEntity.ok(aboutService.update(id, request));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<CustomResponse> deleteAbout(
            @PathVariable Long id) {
        return ResponseEntity.ok(aboutService.delete(id));
    }
}

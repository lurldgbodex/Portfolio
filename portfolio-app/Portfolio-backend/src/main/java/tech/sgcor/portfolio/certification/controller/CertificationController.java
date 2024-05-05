package tech.sgcor.portfolio.certification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.certification.dto.CertificationDto;
import tech.sgcor.portfolio.certification.service.CertificationService;
import tech.sgcor.portfolio.certification.dto.UpdateCertification;
import tech.sgcor.portfolio.certification.dto.UpdateDetailRequest;
import tech.sgcor.portfolio.certification.entity.Certification;
import tech.sgcor.portfolio.shared.CustomResponse;

@RestController
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admins/certifications")
public class CertificationController {
    private final CertificationService service;

    @PostMapping("/add")
    public ResponseEntity<Certification> addCertification(
            @RequestBody @Valid CertificationDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addCertification(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateCertification(
            @PathVariable Long id, @RequestBody @Valid UpdateCertification request) {
        return ResponseEntity.ok(service.updateCertification(id, request));
    }

    @PatchMapping("/details/{id}")
    public ResponseEntity<CustomResponse> updateCertificationDetail(
            @PathVariable Long id, @RequestBody @Valid UpdateDetailRequest request) {
        return ResponseEntity.ok(service.updateCertificationDetail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteCertification(@PathVariable Long id){
        return ResponseEntity.ok(service.deleteCertification(id));
    }
}

package tech.sgcor.portfolio.certification;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin/certifications")
public class CertificationController {
    private final CertificationService service;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> addCertification(
            @RequestBody CertificationDto request, UriComponentsBuilder ucb) {
        var certification = service.addCertification(request);
        URI location = ucb.path(SharedService.BASE_URL + "{id}")
                .buildAndExpand(certification.getUserId()).toUri();
        var res = new CustomResponse(201,
                "certification created successfully", HttpStatus.CREATED);
        return ResponseEntity.created(location).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateCertification(
            @PathVariable Long id, @RequestBody UpdateCertification request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateCertification(id, request));
    }

    @PatchMapping("/details/{id}")
    public ResponseEntity<CustomResponse> updateCertificationDetail(
            @PathVariable Long id, @RequestBody UpdateDetailRequest request) throws ResourceNotFound {
        return ResponseEntity.ok(service.updateCertificationDetail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteCertification(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteCertification(id));
    }
}

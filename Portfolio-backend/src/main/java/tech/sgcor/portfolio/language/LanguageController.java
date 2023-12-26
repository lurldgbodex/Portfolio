package tech.sgcor.portfolio.language;

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
@RequestMapping("/api/admin/languages")
public class LanguageController {
    private final LanguageService service;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> createLanguage(
            @RequestBody LanguageDto request, UriComponentsBuilder ucb) {
        var language = service.createLanguage(request);

        URI location = ucb.path(SharedService.BASE_URL + "{id}")
                .buildAndExpand(language.getId()).toUri();

        var res = new CustomResponse(201, "language created successfully", HttpStatus.CREATED);
        return ResponseEntity.created(location).body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> updateLanguage(
            @PathVariable long id, @RequestBody LanguageUpdate request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateLanguage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteLanguage(
            @PathVariable long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteLanguage(id));
    }
}

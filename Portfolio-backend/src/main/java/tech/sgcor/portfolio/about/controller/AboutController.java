package tech.sgcor.portfolio.about;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.portfolio.shared.CustomResponse;

@RestController
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
@RequestMapping("/api/admins/abouts")
public class AboutController {
    private final AboutService aboutService;

    @PostMapping("/add")
    private ResponseEntity<About> addAbout(@RequestBody @Valid CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aboutService.add(request));
    }

    @PatchMapping("/{id}")
    private ResponseEntity<CustomResponse> updateAbout(
            @PathVariable Long id, @RequestBody @Valid UpdateRequest request) {
        return ResponseEntity.ok(aboutService.update(id, request));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<CustomResponse> deleteAbout(
            @PathVariable Long id) {
        return ResponseEntity.ok(aboutService.delete(id));
    }
}

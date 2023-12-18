package tech.sgcor.portfolio.about;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/abouts")
public class AboutController {
    private final AboutService aboutService;

    @GetMapping("/{id}")
    private ResponseEntity<AboutResponse> getAbout(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(aboutService.getAbout(id));
    }

    @PostMapping("/add")
    private ResponseEntity<CustomResponse> addAbout(
            @RequestBody CreateRequest request, UriComponentsBuilder ucb) {
        About about = aboutService.add(request);
        URI newAbout = ucb
                .path("/api/abouts/{id}")
                .buildAndExpand(about.getId()).toUri();


        return ResponseEntity.created(newAbout)
                .body(new CustomResponse(HttpStatus.CREATED.value(),
                        "Resource successfully created", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    private ResponseEntity<CustomResponse> updateAbout(
            @PathVariable Long id, @RequestBody UpdateRequest request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(aboutService.update(id, request));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<CustomResponse> deleteAbout(
            @PathVariable Long id) throws ResourceNotFound {
        return ResponseEntity.ok(aboutService.delete(id));
    }
}

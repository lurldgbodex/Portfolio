package tech.sgcor.portfolio.skills;

import jakarta.validation.Valid;
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
@RequestMapping("/api/admins/skills")
public class SkillController {
    private final SkillService service;

    @PostMapping("/add")
    public ResponseEntity<SkillType> createSkill(
            @RequestBody @Valid SkillRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createSkill(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateSkills(@PathVariable long id,
            @RequestBody @Valid SkillUpdateRequest request) {
        return ResponseEntity.ok(service.updateSkill(request, id));
    }

    @PatchMapping("/about/{id}")
    public ResponseEntity<CustomResponse> update(
            @PathVariable long id, @RequestBody @Valid UpdateSkill request) {
        return ResponseEntity.ok(service.updateSpecificSkill(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteSkill(
            @PathVariable long id) {
        return ResponseEntity.ok(service.deleteSkill(id));
    }
}

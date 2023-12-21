package tech.sgcor.portfolio.skills;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService service;

    @GetMapping("/{id}")
    public ResponseEntity<GetSkill> getSkills(
            @PathVariable long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.getSkills(id));
    }

    @PostMapping("/new")
    public ResponseEntity<CustomResponse> createSkill(
            @RequestBody SkillRequest request, UriComponentsBuilder ucb) {
        var skills = service.createSkill(request);
        URI location = ucb.path("/api/skills/{id}").buildAndExpand(skills.getId()).toUri();

        var res = new CustomResponse(201, "skill created successfully", HttpStatus.CREATED);

        return ResponseEntity.created(location).body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> updateSkill(@PathVariable long id,
            @RequestBody SkillUpdateRequest request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateSkill(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteSkill(
            @PathVariable long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteSkill(id));
    }
}

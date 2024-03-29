package tech.sgcor.portfolio.skills;

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
@RequestMapping("/api/admin/skills")
public class SkillController {
    private final SkillService service;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> createSkill(
            @RequestBody SkillRequest request, UriComponentsBuilder ucb) {
        var skills = service.createSkill(request);
        URI location = ucb.path(SharedService.BASE_URL + "{id}")
                .buildAndExpand(skills.getId()).toUri();

        var res = new CustomResponse(201, "skill created successfully", HttpStatus.CREATED);

        return ResponseEntity.created(location).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateSkills(@PathVariable long id,
            @RequestBody SkillUpdateRequest request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateSkill(request, id));
    }

    @PatchMapping("/about/{id}")
    public ResponseEntity<CustomResponse> update(
            @PathVariable long id, @RequestBody UpdateSkill request) throws ResourceNotFound, BadRequestException {
        return ResponseEntity.ok(service.updateSpecificSkill(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteSkill(
            @PathVariable long id) throws ResourceNotFound {
        return ResponseEntity.ok(service.deleteSkill(id));
    }
}

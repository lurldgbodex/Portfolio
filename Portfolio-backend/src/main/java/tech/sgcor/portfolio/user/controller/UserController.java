package tech.sgcor.portfolio.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5173")
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserData(
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getUSerData(userId));
    }
}

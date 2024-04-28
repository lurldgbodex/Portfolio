package tech.sgcor.portfolio.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserData(
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getUSerData(userId));
    }
}

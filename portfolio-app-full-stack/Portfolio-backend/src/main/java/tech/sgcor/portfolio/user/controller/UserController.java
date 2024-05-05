package tech.sgcor.portfolio.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.portfolio.user.dto.UserDto;
import tech.sgcor.portfolio.user.service.UserService;

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

package tech.sgcor.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.user.dto.*;
import tech.sgcor.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByEmail(@RequestBody String email) {
        return userService.getUserDetails(email);
    }

    @PostMapping("/create")
    public ResponseEntity<CustomResponse> createNewUser(
            @RequestBody @Valid CreateUserRequest request) {
        var res = userService.registerUser(request);
        return ResponseEntity.created(res.location())
                .body(res.body());
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse updateUserDetail(@RequestBody UpdateUserDetails request) {
        return userService.updateUserDetails(request);
    }

    @PutMapping("/change-password")
    public ResponseEntity<CustomResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<CustomResponse> confirmAccount(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.confirmAccount(token));
    }

    @PutMapping("/password/reset")
    public ResponseEntity<CustomResponse> requestPasswordReset(@RequestBody String email) {
        return ResponseEntity.ok(userService.requestPasswordReset(email));
    }

    @PutMapping("/password/reset/confirm")
    public ResponseEntity<CustomResponse> resetPassword(
            @RequestParam String token, @RequestBody @Valid ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(token, request));
    }
}

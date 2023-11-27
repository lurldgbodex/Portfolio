package com.sgcor.shopply.user;

import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    public final UserService userService;
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> updateUser(@RequestBody UserDetailDTO request) {
        try {
            return ResponseEntity.ok(userService.updateUser(request));
        } catch (RuntimeException re) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(re.getMessage()));
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            return ResponseEntity.ok(userService.getUserDetails());
        } catch (Exception e) {
            return ResponseEntity.
                    status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{confirmationToken}")
    public ResponseEntity<GenericResponse> confirmEmail(@PathVariable String confirmationToken) {
        try {
            return ResponseEntity.ok(userService.confirmUserEmail(confirmationToken));
        } catch (BadRequestException bre) {
            return ResponseEntity.badRequest().body(new GenericResponse(bre.getMessage()));
        }
    }
}

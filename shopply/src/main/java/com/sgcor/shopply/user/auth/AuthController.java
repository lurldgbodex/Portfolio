package com.sgcor.shopply.user.auth;

import com.sgcor.shopply.shared.ErrorResponse;
import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/auths")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthDTO authDTO) {
        try {

            return ResponseEntity
                    .ok(new AuthResponse(authService.registerUser(authDTO)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        try {
            return ResponseEntity.ok(new AuthResponse(authService.loginUser(authDTO)));

        } catch (BadRequestException bre) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(bre.getMessage()));
        } catch (UsernameNotFoundException unf) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(unf.getMessage()));
        } catch (UnauthorizedException uae) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(uae.getMessage()));
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<GenericResponse> updatePassword(@RequestBody PasswordChangeRequest request) {
        try{
            authService.changePassword(request);
            return ResponseEntity.ok(new GenericResponse("password updated successfully"));
        } catch (IllegalStateException ise) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse(ise.getMessage()));
        }

    }
}


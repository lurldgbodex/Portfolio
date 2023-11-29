package com.sgcor.shopply.user.auth;

import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
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
                    .status(HttpStatus.CREATED)
                    .body(new AuthResponse(authService.registerUser(authDTO)));
        } catch (BadRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        try {
            return ResponseEntity.ok(new AuthResponse(authService.loginUser(authDTO)));

        } catch (BadRequestException bre) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse(bre.getMessage()));
        } catch (UsernameNotFoundException unf) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(unf.getMessage()));
        } catch (UnauthorizedException uae) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new GenericResponse(uae.getMessage()));
        }
    }
}


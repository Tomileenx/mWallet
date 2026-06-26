package com.example.naijaWallet.auth;

import com.example.naijaWallet.refreshToken.RefreshRequest;
import com.example.naijaWallet.refreshToken.VerifyRefreshResponse;
import com.example.naijaWallet.refreshToken.RefreshTokenService;
import com.example.naijaWallet.userAccount.UserResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/naijaWallet")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid  @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<VerificationResponse> verify(
            @RequestParam String token
    ) {
        VerificationResponse response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<VerifyRefreshResponse> refreshToken(@RequestBody RefreshRequest request) {

        VerifyRefreshResponse response =
                refreshTokenService.verifyToken(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String email) {
        refreshTokenService.logout(email);
        return ResponseEntity.noContent().build();
    }
}


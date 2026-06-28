package com.example.naijaWallet.auth;

import com.example.naijaWallet.refreshToken.RefreshRequest;
import com.example.naijaWallet.refreshToken.VerifyRefreshResponse;
import com.example.naijaWallet.refreshToken.RefreshTokenService;
import com.example.naijaWallet.userAccount.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Register mWallet User")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/409"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid  @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Verify mWallet User")
    @ApiResponses({
            @ApiResponse(responseCode = "400", ref = "#/components/responses/400"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<VerificationResponse> verify(
            @RequestParam String token
    ) {
        VerificationResponse response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "mWallet User Login")
    @ApiResponses({
            @ApiResponse(responseCode = "400", ref = "#/components/responses/400"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/401"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "mWallet Refresh Token")
    @ApiResponses ({
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @PostMapping("/refreshToken")
    public ResponseEntity<VerifyRefreshResponse> refreshToken(@RequestBody RefreshRequest request) {

        VerifyRefreshResponse response =
                refreshTokenService.verifyToken(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "mWallet Log Out")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/404")
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String email) {
        refreshTokenService.logout(email);
        return ResponseEntity.noContent().build();
    }
}


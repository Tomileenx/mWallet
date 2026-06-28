package com.example.naijaWallet.util;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/naijaWallet")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @Operation(summary = "Resend mWallet User Verification")
    @ApiResponses({
            @ApiResponse(responseCode = "400", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/500"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(
            @RequestBody ResendVerificationRequest request
    ) {

       verificationService.resendVerificationEmail(request);

        return ResponseEntity.ok(
                "If the account exists and is not verified, a verification email has been sent."
        );
    }
}

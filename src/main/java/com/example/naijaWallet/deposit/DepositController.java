package com.example.naijaWallet.deposit;

import com.example.naijaWallet.config.UserPrincipal;
import com.example.naijaWallet.userAccount.UserAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/naijaWallet")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> fund(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody DepositRequest request
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        DepositResponse response = depositService.fund(userAccount, idempotencyKey, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallet/deposits/{depositId}")
    public ResponseEntity<DepositResponse> getDepositById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID depositId
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        DepositResponse response = depositService.getDepositById(userAccount, depositId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallet/deposits")
    public ResponseEntity<Page<DepositResponse>> getAllDeposits(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject
            @PageableDefault(
                    page = 0
            )
            Pageable pageable
    ) {
        UserAccount userAccount = userPrincipal.getUser();

        Page<DepositResponse> response =
                depositService.getAllDeposits(userAccount, pageable);

        return ResponseEntity.ok(response);
    }
}

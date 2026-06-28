package com.example.naijaWallet.wallet;

import com.example.naijaWallet.deposit.DepositRequest;
import com.example.naijaWallet.deposit.DepositResponse;
import com.example.naijaWallet.transaction.TransactionResponse;
import com.example.naijaWallet.transaction.TransactionService;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.config.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/naijaWallet")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {
    private final WalletService walletService;
    private final TransactionService transactionService;

    @Operation(summary = "My mWallet Wallet")
    @ApiResponses({
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallet")
    public ResponseEntity<WalletResponse> getWallet(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        WalletResponse response = walletService.getMyWallet(userAccount);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "My mWallet Transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallet/transactions")
    public ResponseEntity<Page<TransactionResponse>> getWalletActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject
            @PageableDefault(
                    page = 0
            )
            Pageable pageable
    ) {
        UserAccount userAccount = userPrincipal.getUser();

        Page<TransactionResponse> response = transactionService.getMyWalletActivities(userAccount, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "My mWallet Transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallet/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> getWalletActivity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID transactionId
    ) {
        UserAccount userAccount = userPrincipal.getUser();

        TransactionResponse response = transactionService.getMyWalletActivity(userAccount, transactionId);

        return ResponseEntity.ok(response);
    }
}

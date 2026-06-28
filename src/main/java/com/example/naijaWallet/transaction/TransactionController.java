package com.example.naijaWallet.transaction;

import com.example.naijaWallet.config.UserPrincipal;
import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userAccount.UserResponse;
import com.example.naijaWallet.userAccount.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/naijaWallet/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @Operation(summary = "All mWallet User Transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallets/{accountNumber}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions(
            @PathVariable String accountNumber,
            @ParameterObject
            @PageableDefault(
                    page = 0
            )
            Pageable pageable
    ) {
        Page<TransactionResponse> response = transactionService.getWalletTransactions(
                accountNumber,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "mWallet User Transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallets/{accountNumber}/transactions/{reference}")
    public ResponseEntity<TransactionResponse> getUserTransaction(
            @PathVariable String accountNumber,
            @PathVariable String reference
    ) {
        TransactionResponse response = transactionService.getWalletTransaction(
                accountNumber,
                reference
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "All mWallet Transactions")
    @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) BalanceType balanceType,
            @ParameterObject
            @PageableDefault(
                page = 0
            )
            Pageable pageable
    ) {
        Page<TransactionResponse> response = transactionService.getAllTransactions(
                transactionType,
                balanceType,
                pageable
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "mWallet User")
    @ApiResponses({
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/account/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID userId
    ) {
        UserResponse response = userService.getUser(userId);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "All mWallet Users")
    @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    @GetMapping("/account/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @ParameterObject
            @PageableDefault(
                    page = 0
            )
            Pageable pageable
    ) {
        Page<UserResponse> response = userService.getAllUsers(pageable);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

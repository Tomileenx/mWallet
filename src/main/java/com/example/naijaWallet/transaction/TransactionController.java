package com.example.naijaWallet.transaction;

import com.example.naijaWallet.config.UserPrincipal;
import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.userAccount.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/naijaWallet/admin")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

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

    @GetMapping("/wallets/transactions")
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
}

package com.example.naijaWallet.wallet;

import com.example.naijaWallet.deposit.DepositRequest;
import com.example.naijaWallet.deposit.DepositResponse;
import com.example.naijaWallet.transaction.TransactionResponse;
import com.example.naijaWallet.transaction.TransactionService;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.config.UserPrincipal;
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
public class WalletController {
    private final WalletService walletService;
    private final TransactionService transactionService;

    @GetMapping("/wallet")
    public ResponseEntity<WalletResponse> getWallet(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        WalletResponse response = walletService.getMyWallet(userAccount);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/wallet/transactions")
    public ResponseEntity<Page<TransactionResponse>> getWalletActivity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject
            @PageableDefault(
                    page = 0
            )
            Pageable pageable
    ) {
        UserAccount userAccount = userPrincipal.getUser();

        Page<TransactionResponse> response = transactionService.getMyWalletActivity(userAccount, pageable);

        return ResponseEntity.ok(response);
    }
}

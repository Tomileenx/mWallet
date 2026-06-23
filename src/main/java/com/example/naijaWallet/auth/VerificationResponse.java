package com.example.naijaWallet.auth;

import com.example.naijaWallet.wallet.WalletResponse;

public record VerificationResponse(
        String message,
        WalletResponse wallet
) {
}

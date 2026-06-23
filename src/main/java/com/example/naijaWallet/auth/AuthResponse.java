package com.example.naijaWallet.auth;


import com.example.naijaWallet.wallet.WalletResponse;

public record AuthResponse(
        String fullName,
        String token,
        String refreshToken
) {
}

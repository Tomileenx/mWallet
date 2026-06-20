package com.example.naijaWallet.userAccount;


import com.example.naijaWallet.wallet.WalletResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String passwordHash,
        LocalDateTime createdAt,
        String message
) {
}

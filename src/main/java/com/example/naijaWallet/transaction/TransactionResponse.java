package com.example.naijaWallet.transaction;

import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID wallet_id,
        TransactionType transactionType,
        BalanceType balanceType,
        BigDecimal amount,
        String transactionReference,

        LocalDateTime createdAt
) {
}

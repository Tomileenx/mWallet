package com.example.naijaWallet.deposit;

import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionStatus;
import com.example.naijaWallet.enumTypes.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositResponse(
        UUID depositId,
        UUID toWalletId,
        String transactionReference,
        BigDecimal amount,
        TransactionStatus transactionStatus,
        LocalDateTime createdAt
) {
}

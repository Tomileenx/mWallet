package com.example.naijaWallet.userEvent;

import com.example.naijaWallet.enumTypes.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditAlertEvent(
        String email,
        UUID transactionId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        String transactionReference,
        LocalDateTime createdAt,
        BigDecimal balance
) {
}

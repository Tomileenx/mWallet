package com.example.naijaWallet.transfer;

import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionStatus;
import com.example.naijaWallet.enumTypes.TransactionType;
import org.hibernate.sql.results.graph.collection.internal.BagInitializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transferId,
        UUID fromWalletId,
        UUID toWalletId,
        BigDecimal amount,
        String transactionReference,
        String transferType,
        TransactionStatus transactionStatus,
        LocalDateTime createdAt
) {
}

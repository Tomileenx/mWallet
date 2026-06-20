package com.example.naijaWallet.transfer;

import com.example.naijaWallet.enumTypes.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        UUID toWalletId,
        BigDecimal amount
) {
}

package com.example.naijaWallet.deposit;

import java.math.BigDecimal;

public record DepositRequest(
        BigDecimal amount
) {
}

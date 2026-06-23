package com.example.naijaWallet.userEvent;

import java.math.BigDecimal;

public record WalletEvent(
        String email,
        String accountNumber,
        BigDecimal balance,
        String currency
) {
}

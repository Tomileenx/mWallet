package com.example.naijaWallet.wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
    UUID id,
    String accountNumber,
    BigDecimal balance,
    String currency
) {
}

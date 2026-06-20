package com.example.naijaWallet.wallet;

import com.example.naijaWallet.deposit.DepositRequest;
import com.example.naijaWallet.deposit.DepositResponse;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionStatus;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.exception.BadRequest;
import com.example.naijaWallet.exception.NotFound;
import com.example.naijaWallet.transaction.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WalletService {
    private final WalletRepo walletRepo;
    private final WalletMapper walletMapper;

    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID walletId) {
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new NotFound("Wallet not found"));

        return walletMapper.toResponse(wallet);
    }


//    public static String maskAccountNumber(String value) {
//
//        if (value == null || value.length() <= 4) {
//            return value;
//        }
//
//        int startVisible = 1;
//        int endVisible = 2;
//
//        String start = value.substring(0, startVisible);
//        String end = value.substring(value.length() - endVisible);
//
//        return start + "XX.." + end;
//    }
}

package com.example.naijaWallet.transaction;

import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.exception.NotFound;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.wallet.Wallet;
import com.example.naijaWallet.wallet.WalletRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepo transactionRepo;
    private final WalletRepo walletRepo;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getWalletTransactions(String accountNumber, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 10),
                Sort.by("createdAt").descending()
        );

        Wallet wallet = walletRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFound("Account number not found"));

        return transactionRepo.findByWallet(wallet, pageRequest)
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getId(),
                        transaction.getTransactionType(),
                        transaction.getBalanceType(),
                        transaction.getAmount(),
                        transaction.getTransactionReference(),
                        transaction.getCreatedAt()
                ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public TransactionResponse getWalletTransaction(String accountNumber, String reference) {
        Wallet wallet = walletRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFound("Account number not found"));

        Transaction transaction = transactionRepo.findByWalletAndTransactionReference(
                wallet,
                reference
        ).orElseThrow(() -> new NotFound("Transaction not found"));

        return new TransactionResponse(
                transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getTransactionType(),
                transaction.getBalanceType(),
                transaction.getAmount(),
                transaction.getTransactionReference(),
                transaction.getCreatedAt()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(
            TransactionType transactionType,
            BalanceType balanceType,
            Pageable pageable
    ) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 10),
                Sort.by("createdAt").descending()
        );

        Page<Transaction> page;

        if (transactionType != null && balanceType != null) {
            page = transactionRepo.findByTransactionTypeAndBalanceType(
                    transactionType,
                    balanceType,
                    pageRequest
            );
        } else if (transactionType != null) {
            page = transactionRepo.findByTransactionType(
                    transactionType,
                    pageRequest
            );
        } else if (balanceType != null) {
            page = transactionRepo.findByBalanceType(
                    balanceType,
                    pageRequest
            );
        } else {
            page = transactionRepo.findAll(pageable);
        }

        return  page
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getId(),
                        transaction.getTransactionType(),
                        transaction.getBalanceType(),
                        transaction.getAmount(),
                        transaction.getTransactionReference(),
                        transaction.getCreatedAt()
                ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getMyWalletActivity(
            UserAccount userAccount,
            Pageable pageable
    ) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 10),
                Sort.by("createdAt").descending()
        );

        Wallet wallet = userAccount.getWallet();

        return transactionRepo.findByWallet(wallet, pageRequest)
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getId(),
                        transaction.getTransactionType(),
                        transaction.getBalanceType(),
                        transaction.getAmount(),
                        transaction.getTransactionReference(),
                        transaction.getCreatedAt()
                ));

    }

    public static String generateTransactionReference() {
       String random = UUID.randomUUID()
               .toString()
               .replace("-", "")
               .substring(0, 8)
               .toUpperCase();

       return "TXN_" + random;
    }
}

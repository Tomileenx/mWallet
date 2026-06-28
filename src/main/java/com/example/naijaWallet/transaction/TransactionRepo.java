package com.example.naijaWallet.transaction;

import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.wallet.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, UUID> {
    // All transactions for a wallet
    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);

    // Wallet transaction
    Optional<Transaction> findByIdAndWallet(UUID transactionId, Wallet wallet);

    // TransactionType and BalanceType
    Page<Transaction> findByTransactionTypeAndBalanceType(
            TransactionType transactionType,
            BalanceType balanceType,
            Pageable pageable
    );

    // BalanceType
    Page<Transaction> findByBalanceType(BalanceType balanceType, Pageable pageable);

    // TransactionType
    Page<Transaction> findByTransactionType(TransactionType type, Pageable pageable);

    // TransactionReference
    Optional<Transaction> findByWalletAndTransactionReference(Wallet wallet, String transactionReference);
}

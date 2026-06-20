package com.example.naijaWallet.wallet;

import com.example.naijaWallet.userAccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, UUID> {

    // Atomic sql preventing race condition
    @Modifying
    @Transactional
    @Query("""
        update Wallet w\s
        set w.balance = w.balance + :amount,
            w.updatedAt = current_timestamp\s
        where w.userAccount = :userAccount
       \s""")
    int walletDeposit(
            @Param("userAccount") UserAccount userAccount,
            @Param("amount") BigDecimal amount
    );

    @Modifying
    @Transactional
    @Query("""
        update Wallet w\s
        set w.balance = w.balance - :amount,
            w.updatedAt = current_timestamp\s
        where w.userAccount = :userAccount
       \s""")
    void debitWallet(
            @Param("userAccount") UserAccount userAccount,
            @Param("amount")BigDecimal amount
    );

    @Modifying
    @Transactional
    @Query("""
        update Wallet w\s
        set w.balance = w.balance + :amount,
            w.updatedAt = current_timestamp\s
        where w.id = :walletId
       \s""")
    void creditWallet(
            @Param("walletId") UUID toWalletId,
            @Param("amount")BigDecimal amount
    );

    Optional<Wallet> findByAccountNumber(String accountNumber);
}

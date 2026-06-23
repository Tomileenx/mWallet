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

    // Atomic sql deposit preventing race condition (read, modify, write)
    @Modifying(clearAutomatically = true, flushAutomatically = true) // clears stale values after deposit
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

    // Atomic sql debit preventing race condition (read, modify, write)
    @Modifying(clearAutomatically = true, flushAutomatically = true) // clears stale values after debit
    @Transactional
    @Query("""
        update Wallet w\s
        set w.balance = w.balance - :amount,
            w.updatedAt = current_timestamp\s
        where w.userAccount = :userAccount
        and w.balance >= :amount
       \s""")
    int debitWallet(
            @Param("userAccount") UserAccount userAccount,
            @Param("amount")BigDecimal amount
    );

    // Atomic sql credit preventing race condition (read, modify, write)
    @Modifying(clearAutomatically = true, flushAutomatically = true) // clears stale values after credit
    @Transactional
    @Query("""
        update Wallet w\s
        set w.balance = w.balance + :amount,
            w.updatedAt = current_timestamp\s
        where w.id = :walletId
       \s""")
    int creditWallet(
            @Param("walletId") UUID toWalletId,
            @Param("amount")BigDecimal amount
    );

    // getting the correct updated wallet balance
    @Query("""
        select w.balance
        from Wallet w
        where w.id = :walletId
    """)
    BigDecimal findBalanceById(@Param("walletId") UUID walletId);

    Optional<Wallet> findByAccountNumber(String accountNumber);
}

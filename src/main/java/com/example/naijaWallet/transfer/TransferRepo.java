package com.example.naijaWallet.transfer;

import com.example.naijaWallet.deposit.Deposit;
import com.example.naijaWallet.wallet.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepo extends JpaRepository<Transfer, UUID> {
    // Get transfer by ID
    @Query("""
        select t
        from Transfer t
        where t.id = :transferId
        and (
            t.fromWallet = :wallet
            or t.toWallet = :wallet
        )
    """)
    Optional<Transfer> findUserTransfer(
           @Param("transferId") UUID transferId,
           @Param("wallet") Wallet wallet
    );

    // Get full user transfers history (sent & received)
    Page<Transfer> findByFromWalletOrToWallet(
            Wallet fromWallet,
            Wallet toWallet,
            Pageable pageable
    );

}

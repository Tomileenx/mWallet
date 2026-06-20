package com.example.naijaWallet.deposit;

import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.wallet.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DepositRepo extends JpaRepository<Deposit, UUID> {
    Optional<Deposit> findByIdAndToWallet(
            UUID id,
            Wallet toWallet
    );

    Page<Deposit> findByToWallet(Wallet wallet, Pageable pageable);
}

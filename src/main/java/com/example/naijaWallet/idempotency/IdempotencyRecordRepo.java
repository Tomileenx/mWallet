package com.example.naijaWallet.idempotency;

import com.example.naijaWallet.userAccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepo extends JpaRepository<IdempotencyRecord, UUID> {
}

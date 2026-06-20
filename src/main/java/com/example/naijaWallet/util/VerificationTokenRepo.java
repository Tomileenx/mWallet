package com.example.naijaWallet.util;

import com.example.naijaWallet.userAccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUserAccount(
            UserAccount user
    );

    void deleteByExpiresAtBefore(LocalDateTime time);
}

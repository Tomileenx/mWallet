package com.example.naijaWallet.util;

import com.example.naijaWallet.exception.BadRequest;
import com.example.naijaWallet.exception.NotFound;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userAccount.UserRepo;
import com.example.naijaWallet.userEvent.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationService {
    private final UserRepo repo;
    private final VerificationTokenRepo verificationTokenRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void resendVerificationEmail(
            ResendVerificationRequest request
    ) {

        UserAccount user = repo
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(
                        () -> new NotFound(
                                "User not found"
                        )
                );

        if (user.isVerified()) {
            throw new BadRequest(
                    "Email already verified"
            );
        }

        verificationTokenRepo
                .deleteByUserAccount(user);

        String verificationToken =
                UUID.randomUUID().toString();

        VerificationToken token =
                new VerificationToken();

        token.setUserAccount(user);
        token.setToken(verificationToken);
        token.setExpiresAt(
                LocalDateTime.now().plusHours(24)
        );

        verificationTokenRepo.save(token);

        log.info("Resending verification email");
        eventPublisher.publishEvent(
                new UserCreatedEvent(
                        user.getEmail(),
                        verificationToken
                )
        );
    }
}

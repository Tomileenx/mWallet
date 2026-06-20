package com.example.naijaWallet.userEvent;

import com.example.naijaWallet.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserCreationEventListener {
    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserCreated(UserCreatedEvent event) {

        String verificationLink =
                "https://naijaWallet/account/verification?token=" + event.token();

        emailService.sendVerificationEmail(
                event.email(),
                verificationLink
        );
    }
}

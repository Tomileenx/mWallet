package com.example.naijaWallet.userEvent;

import com.example.naijaWallet.email.EmailService;
import com.example.naijaWallet.wallet.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onWalletDetails(WalletEvent event) {

        String accountNumber = event.accountNumber();
        BigDecimal balance = event.balance();
        String currency = event.currency();

        log.info("WalletEvent received");

        emailService.sendWalletDetails(
                event.email(),
                accountNumber,
                balance,
                currency
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDepositAlert(DepositAlertEvent event) {
        log.info("DepositAlertEvent received");
        emailService.sendDepositAlert(
                event.email(),
                event.transactionId(),
                event.accountNumber(),
                event.amount(),
                event.currency(),
                event.transactionReference(),
                event.transactionStatus(),
                event.createdAt(),
                event.balance()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreditAlert(CreditAlertEvent event) {
        log.info("CreditAlertEvent received");
        emailService.sendCreditAlert(
                event.email(),
                event.transactionId(),
                event.accountNumber(),
                event.amount(),
                event.currency(),
                event.transactionReference(),
                event.createdAt(),
                event.balance()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDebitAlert(DebitAlertEvent event) {
        log.info("DebitAlertEvent received");
        emailService.sendDebitAlert(
                event.email(),
                event.transactionId(),
                event.accountNumber(),
                event.recipient(),
                event.amount(),
                event.currency(),
                event.transactionStatus(),
                event.transactionReference(),
                event.createdAt(),
                event.balance()
        );
    }
}

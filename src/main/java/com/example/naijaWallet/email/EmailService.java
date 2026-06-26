package com.example.naijaWallet.email;

import com.example.naijaWallet.config.BrevoProperties;
import com.example.naijaWallet.enumTypes.TransactionStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final WebClient webClient;

    private final BrevoProperties brevoProperties;

    public void sendEmail(String to, String subject, String body) {
        Objects.requireNonNull(to, "Recipient email cannot be null");
        Objects.requireNonNull(brevoProperties.getSenderEmail(), "Sender email cannot be null");
        Objects.requireNonNull(brevoProperties.getSenderName(), "Sender name cannot be null");

        try {
            webClient.post()
                    .header("api-key", brevoProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "sender", Map.of(
                                    "name", brevoProperties.getSenderName(),
                                    "email", brevoProperties.getSenderEmail()
                            ),
                            "to", List.of(Map.of("email", to)),
                            "subject", subject,
                            "textContent", body
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Email sent successfully to {}", to);
        } catch (WebClientResponseException e) {
            log.error(
                    "Brevo returned status {}",
                    e.getStatusCode(),
                    e
            );

            throw new RuntimeException("Unable to send email");
        } catch (Exception e) {
            log.error("Unexpected email error", e);

            throw new RuntimeException("Unable to send email");
        }
    }

    public void sendVerificationEmail(String email, String verificationLink) {
        String subject = "Email Verification";

        String body =
                """
                Welcome to mWallet.

                Click the link below to verify your email:

                %s

                This link expires in 24 hours.
                """.formatted(verificationLink);

        sendEmail(email, subject, body);
    }

    public void sendWalletDetails(
            String email,
            String accountNumber,
            BigDecimal balance,
            String currency
    ) {
        String subject = "Email Verification Successful!";

        String body = """
                      Your Wallet is now active.
                     \s
                      Wallet information:
                      Account Number: %s
                      Available Balance: %s %s
                     \s
                      You can now:
                      Deposit funds
                      Transfer money
                      View transaction history
                     \s
                      Welcome aboard!\s
                     \s
                      mWallet Team
                     \s""".formatted(accountNumber, balance, currency);

        sendEmail(email, subject, body);
    }

    public void sendDepositAlert(
            String email,
            UUID transactionId,
            String accountNumber,
            BigDecimal amount,
            String currency,
            String transactionReference,
            TransactionStatus status,
            LocalDateTime createdAt,
            BigDecimal balance
    ) {

        String subject = "mWallet Deposit Alert";

        String body = """
            Dear Customer,

            A deposit has been received into your wallet.

            Deposit Details
            ------------------------------
            Transaction ID: %s
            Account Number: %s
            Amount: %s %s
            Transaction Reference: %s
            Status: %s
            Date: %s

            Current Wallet Balance
            ------------------------------
            Balance: %s %s

            Thank you for using mWallet.

            mWallet Team
            """
                .formatted(
                        transactionId,
                        accountNumber,
                        currency,
                        amount,
                        transactionReference,
                        status,
                        createdAt,
                        currency,
                        balance
                );

        sendEmail(email, subject, body);
    }

    public void sendCreditAlert(
            String email,
            UUID transactionId,
            String accountNumber,
            BigDecimal amount,
            String currency,
            String transactionReference,
            LocalDateTime createdAt,
            BigDecimal balance
    ) {

        String subject = "mWallet Credit Alert";

        String body = """
            Dear Customer,

            A credit has been received into your wallet.

            Credit Details
            ------------------------------
            Transaction ID: %s
            Account Number: %s
            Amount: %s %s
            Transaction Reference: %s
            Date: %s

            Current Wallet Balance
            ------------------------------
            Balance: %s %s

            Thank you for using mWallet.

            mWallet Team
            """
                .formatted(
                        transactionId,
                        accountNumber,
                        currency,
                        amount,
                        transactionReference,
                        createdAt,
                        currency,
                        balance
                );

        sendEmail(email, subject, body);
    }

    public void sendDebitAlert(
            String email,
            UUID transactionId,
            String accountNumber,
            String recipient,
            BigDecimal amount,
            String currency,
            TransactionStatus status,
            String transactionReference,
            LocalDateTime createdAt,
            BigDecimal balance
    ) {

        String subject = "mWallet Debit Alert";

        String body = """
            Dear Customer,

            Funds have been debited from your wallet.

            Transaction Details
            ------------------------------
            Transaction ID: %s
            Account Number: %s
            Recipient Account: %s
            Amount: %s %s
            Transaction Reference: %s
            Status: %s
            Date: %s

            Current Wallet Balance
            ------------------------------
            Balance: %s %s

            Thank you for using mWallet.

            mWallet Team
            """
                .formatted(
                        transactionId,
                        accountNumber,
                        recipient,
                        currency,
                        amount,
                        transactionReference,
                        status,
                        createdAt,
                        currency,
                        balance
                );

        sendEmail(email, subject, body);
    }
}

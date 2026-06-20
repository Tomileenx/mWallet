package com.example.naijaWallet.email;

import com.example.naijaWallet.config.BrevoProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final WebClient webClient;

    private final BrevoProperties brevoProperties;

    @PostConstruct
    public void init() {
        System.out.println("Brevo API Key: " + brevoProperties.getApiKey());
        System.out.println("Sender Email: " + brevoProperties.getSenderEmail());
        System.out.println("Sender Name: " + brevoProperties.getSenderName());
    }

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
                Welcome to NaijaWallet.

                Click the link below to verify your email:

                %s

                This link expires in 24 hours.
                """.formatted(verificationLink);

        sendEmail(email, subject, body);
    }
}

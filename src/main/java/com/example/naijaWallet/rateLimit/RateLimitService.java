package com.example.naijaWallet.rateLimit;

import com.example.naijaWallet.wallet.Wallet;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, RateLimitOperation operation) {
        return buckets.computeIfAbsent(
                key + ":" + operation.name(),
                k -> createBucket(operation)
        );
    }

    private Bucket createBucket(RateLimitOperation operation) {

        return switch (operation) {
            case REGISTER, RESEND_VERIFICATION -> Bucket.builder()
                    .addLimit(
                            Bandwidth.builder()
                                    .capacity(3)
                                    .refillIntervally(3, Duration.ofMinutes(1))
                                    .build()
                    )
                    .build();

            case LOGIN, VERIFICATION, TRANSFER -> Bucket.builder()
                    .addLimit(
                            Bandwidth.builder()
                                    .capacity(5)
                                    .refillIntervally(5, Duration.ofMinutes(1))
                                    .build()
                    ).build();

            case DEPOSIT -> Bucket.builder()
                    .addLimit(
                            Bandwidth.builder()
                                    .capacity(10)
                                    .refillIntervally(10, Duration.ofMinutes(1))
                                    .build()
                    ).build();

            case WALLET  -> Bucket.builder()
                    .addLimit(
                            Bandwidth.builder()
                                    .capacity(20)
                                    .refillIntervally(20, Duration.ofMinutes(1))
                                    .build()
                    ).build();

            case WALLET_TRANSACTIONS, WALLET_DEPOSITS, WALLET_TRANSFERS -> Bucket.builder()
                    .addLimit(
                            Bandwidth.builder()
                                    .capacity(30)
                                    .refillIntervally(30, Duration.ofMinutes(1))
                                    .build()
                    ).build();

            case DEFAULT -> Bucket.builder()
                    .addLimit(
                            Bandwidth.builder()
                                    .capacity(15)
                                    .refillIntervally(15, Duration.ofMinutes(1))
                                    .build()
                    ).build();
        };
    }
}

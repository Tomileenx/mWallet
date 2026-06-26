package com.example.naijaWallet.auth;


import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String fullName,
        String email,
        String passwordHash,
        LocalDateTime createdAt,
        String message
) {
}

package com.example.naijaWallet.auth;

public record RegisterRequest(
        String email,
        String fullName,
        String passwordHash
) {
}

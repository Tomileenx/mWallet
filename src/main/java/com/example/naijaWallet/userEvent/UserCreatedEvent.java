package com.example.naijaWallet.userEvent;

public record UserCreatedEvent(
        String email,
        String token
) {
}

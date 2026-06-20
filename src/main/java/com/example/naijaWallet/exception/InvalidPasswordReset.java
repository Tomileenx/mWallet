package com.example.naijaWallet.exception;

public class InvalidPasswordReset extends RuntimeException {
    public InvalidPasswordReset(String message) {
        super(message);
    }
}

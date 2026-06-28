package com.example.naijaWallet.exception;

public class SessionExpired extends RuntimeException {
    public SessionExpired(String message) {
        super(message);
    }
}

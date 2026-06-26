package com.example.naijaWallet.rateLimit;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        RateLimitOperation operation = resolveOperation(request);

        String key = resolveKey(request, operation);

        Bucket bucket = rateLimitService.resolveBucket(key, operation);

        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

            response.getWriter().write(
                    "Too many requests. Please try again later."
            );

            return false;
        }
        return true;
    }

    private String resolveKey(
            HttpServletRequest request,
            RateLimitOperation operation
    ) {
        if (
                operation == RateLimitOperation.REGISTER
                || operation == RateLimitOperation.LOGIN
                || operation == RateLimitOperation.VERIFICATION
                || operation == RateLimitOperation.RESEND_VERIFICATION
        ) {
            return request.getRemoteAddr();
        }

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return request.getRemoteAddr();
        }

        return authentication.getName();
    }

    private RateLimitOperation resolveOperation(
            HttpServletRequest request
    ) {
        String uri = request.getRequestURI();

        return switch (uri) {

            case "/naijaWallet/register" ->
                    RateLimitOperation.REGISTER;

            case "/naijaWallet/login" ->
                    RateLimitOperation.LOGIN;

            case "/naijaWallet/verification" ->
                    RateLimitOperation.VERIFICATION;

            case "/naijaWallet/resend-verification" ->
                    RateLimitOperation.RESEND_VERIFICATION;

            case "/naijaWallet/deposit" ->
                    RateLimitOperation.DEPOSIT;

            case "/naijaWallet/transfer" ->
                    RateLimitOperation.TRANSFER;

            case "/naijaWallet/wallet" ->
                    RateLimitOperation.WALLET;

            case "/naijaWallet/wallet/transactions" ->
                    RateLimitOperation.WALLET_TRANSACTIONS;

            case "/naijaWallet/wallet/deposits" ->
                    RateLimitOperation.WALLET_DEPOSITS;

            case "/naijaWallet/wallet/transfers" ->
                    RateLimitOperation.WALLET_TRANSFERS;

            default ->
                    RateLimitOperation.DEFAULT;
        };
    }
}

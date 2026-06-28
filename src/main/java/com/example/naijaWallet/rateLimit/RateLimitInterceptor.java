package com.example.naijaWallet.rateLimit;

import com.example.naijaWallet.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

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
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ErrorResponse error = new ErrorResponse(
                    "Too many requests. Please try again later.",
                    "TOO_MANY_REQUESTS",
                    Instant.now()
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), error);


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

        if (uri.startsWith("/naijaWallet/wallet/deposits")) {
            return RateLimitOperation.WALLET_DEPOSITS;
        }

        if (uri.startsWith("/naijaWallet/wallet/transfers")) {
            return RateLimitOperation.WALLET_TRANSFERS;
        }

        if (uri.startsWith("/naijaWallet/wallet/transactions")) {
            return RateLimitOperation.WALLET_TRANSACTIONS;
        }

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

            default ->
                    RateLimitOperation.DEFAULT;
        };
    }
}

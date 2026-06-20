package com.example.naijaWallet.refreshToken;

import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userAccount.UserRepo;
import com.example.naijaWallet.config.JWTService;
import com.example.naijaWallet.config.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final RefreshMapper refreshMapper;
    private final UserRepo userRepo;
    private final JWTService jwtService;

    private final long REFRESH_EXPIRATION = 24 * 60 * 60 * 1000;
    private final long IN_ACTIVITY_LIMIT =  15 * 60 * 1000;

    @Transactional
    public RefreshResponse createRefreshToken(UserAccount user) {
        RefreshToken token = new RefreshToken();
        token.setTokenHash(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(REFRESH_EXPIRATION));
        token.setLastActivity(Instant.now());

        RefreshToken saved = refreshTokenRepo.save(token);
        return refreshMapper.toResponse(saved);
    }

    @Transactional
    public VerifyRefreshResponse verifyToken(RefreshRequest request) {
        RefreshToken rToken = refreshTokenRepo.findByTokenHash(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        if (rToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepo.delete(rToken);
            throw new RuntimeException("Refresh token expired");
        }

        if (rToken.getLastActivity().plusMillis(IN_ACTIVITY_LIMIT).isBefore(Instant.now())) {
            refreshTokenRepo.delete(rToken);
            throw new RuntimeException("Session expired due to inactivity");
        }

        String newAccessToken =
                jwtService.generateToken(
                        new UserPrincipal(rToken.getUser())
                );

        rToken.setLastActivity(Instant.now());
        refreshTokenRepo.save(rToken);

        return new VerifyRefreshResponse(
                newAccessToken
        );
    }

    public void logout(String username) {
        UserAccount user = userRepo.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        refreshTokenRepo.deleteByUser(user);
    }
}

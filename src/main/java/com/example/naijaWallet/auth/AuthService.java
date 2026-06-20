package com.example.naijaWallet.auth;

import com.example.naijaWallet.config.JWTService;
import com.example.naijaWallet.config.UserPrincipal;
import com.example.naijaWallet.exception.AlreadyExists;
import com.example.naijaWallet.exception.BadRequest;
import com.example.naijaWallet.exception.NotFound;
import com.example.naijaWallet.refreshToken.RefreshResponse;
import com.example.naijaWallet.refreshToken.RefreshTokenService;
import com.example.naijaWallet.roles.Role;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userAccount.UserMapper;
import com.example.naijaWallet.userAccount.UserRepo;
import com.example.naijaWallet.userAccount.UserResponse;
import com.example.naijaWallet.userEvent.UserCreatedEvent;
import com.example.naijaWallet.util.VerificationToken;
import com.example.naijaWallet.util.VerificationTokenRepo;
import com.example.naijaWallet.wallet.Wallet;
import com.example.naijaWallet.wallet.WalletRepo;
import com.example.naijaWallet.wallet.WalletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepo repo;
    private final UserMapper userMapper;
    private final WalletRepo walletRepo;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final VerificationTokenRepo verificationTokenRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (repo.existsByEmailIgnoreCase(request.email())) {
            throw new AlreadyExists(request.email() + " already exist");
        }

        UserAccount user = userMapper.toEntity(request);
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.passwordHash()));
        user.setRole(Role.USER);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        repo.save(user);

        String verificationToken =
                UUID.randomUUID().toString();

        VerificationToken token = new VerificationToken();
        token.setUserAccount(user);
        token.setToken(verificationToken);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        verificationTokenRepo.save(token);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getEmail(), verificationToken));


        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getCreatedAt(),
                "Email verification link sent to your email"
        );
    }

    public void VerifyEmail(String tokenValue) {
        VerificationToken token = verificationTokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new NotFound("Invalid token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequest("Verification token expired");
        }

        UserAccount user = token.getUserAccount();

        if (!user.isVerified()) {
            user.setVerified(true);
            createWallet(user);

            repo.save(user);
        }

        verificationTokenRepo.delete(token);
    }

    public void createWallet(UserAccount user) {
        if (user.getRole() == Role.USER) {

            Wallet wallet = new Wallet();
            wallet.setUserAccount(user);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCurrency("NGN");
            wallet.setCreatedAt(LocalDateTime.now());
            wallet.setUpdatedAt(LocalDateTime.now());

            boolean saved = false;
            int attempts = 0;

            // Race condition check
            while (!saved && attempts < 3) {
                try {
                    wallet.setAccountNumber(generateAccountNumber());
                    walletRepo.save(wallet);
                    saved = true;
                } catch (DataIntegrityViolationException e) {
                    attempts++;
                    if (attempts >= 3) {
                        throw new RuntimeException("Unable to generate unique account number, please try again");
                    }
                }
            }

            user.setWallet(wallet);
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.passwordHash()
        ));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserAccount user = principal.getUser();

        if (!user.isVerified()) {
            throw new BadRequest("Please verify your email first");
        }

        String token = jwtService.generateToken(principal);
        RefreshResponse refresh = refreshTokenService.createRefreshToken(user);

        Wallet wallet = user.getWallet();

        return new AuthResponse(
                user.getFullName(),
                new WalletResponse(
                        wallet.getId(),
                        wallet.getAccountNumber(),
                        wallet.getBalance(),
                        wallet.getCurrency()
                ),
                token,
                refresh.tokenHash()
        );
    }

    private String generateAccountNumber() {
        return "WAL" + UUID.randomUUID().toString().substring(0, 10);
    }
}

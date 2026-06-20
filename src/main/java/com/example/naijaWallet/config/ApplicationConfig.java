package com.example.naijaWallet.config;

import com.example.naijaWallet.roles.Role;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userAccount.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepo userRepo;

    @Bean
    CommandLineRunner commandLineRunner(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepo.findByEmailIgnoreCase("admin@example.com").isEmpty()) {
                UserAccount admin = new UserAccount();
                admin.setFullName("System Admin");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(passwordEncoder.encode("adminPassword"));
                admin.setRole(Role.ADMIN);
                admin.setVerified(true);

                userRepo.save(admin);
            }
        };
    }
}

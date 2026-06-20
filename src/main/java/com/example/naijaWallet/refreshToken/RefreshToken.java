package com.example.naijaWallet.refreshToken;

import com.example.naijaWallet.userAccount.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String tokenHash;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount user;

    private Instant expiryDate;

    private Instant lastActivity;
}

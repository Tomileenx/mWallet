package com.example.naijaWallet.idempotency;


import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.userAccount.UserAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "idempotency_records",
        uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {
                        "user_account_id",
                        "transaction_type",
                        "idempotency_key"
                }
        )
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class IdempotencyRecord {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @Column(nullable = false)
    private String transactionReference;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

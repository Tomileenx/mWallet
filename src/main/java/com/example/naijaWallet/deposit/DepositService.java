package com.example.naijaWallet.deposit;

import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionStatus;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.exception.BadRequest;
import com.example.naijaWallet.exception.NotFound;
import com.example.naijaWallet.transaction.Transaction;
import com.example.naijaWallet.transaction.TransactionRepo;
import com.example.naijaWallet.transaction.TransactionService;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userEvent.DebitAlertEvent;
import com.example.naijaWallet.userEvent.DepositAlertEvent;
import com.example.naijaWallet.wallet.Wallet;
import com.example.naijaWallet.wallet.WalletMapper;
import com.example.naijaWallet.wallet.WalletRepo;
import com.example.naijaWallet.wallet.WalletService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositService {
    private final DepositRepo depositRepo;
    private final TransactionRepo transactionRepo;
    private final WalletRepo walletRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public DepositResponse fund(UserAccount userAccount, DepositRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.TEN) < 0) {
            throw new BadRequest("Minimum deposit amount is 10");
        }

        int updatedRows = walletRepo.walletDeposit(userAccount, request.amount());
        log.info(" Initiating deposit of {} to {}",
                request.amount(),
                userAccount.getWallet()
        );

        if (updatedRows == 0) {
            throw new NotFound("Wallet not found");
        }

        //  ensure correct balance in memory
        Wallet wallet = walletRepo.findById(
                userAccount.getWallet().getId()
        ).orElseThrow(() -> new NotFound("Wallet not found"));

        String depositReference  = TransactionService.generateTransactionReference();

        Deposit deposit = new Deposit();
        deposit.setToWallet(userAccount.getWallet());
        deposit.setAmount(request.amount());
        deposit.setTransactionReference(depositReference);
        deposit.setTransactionStatus(TransactionStatus.SUCCESS);
        deposit.setCreatedAt(LocalDateTime.now());

        depositRepo.save(deposit);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setBalanceType(BalanceType.CREDIT);
        transaction.setAmount(request.amount());
        transaction.setTransactionReference(depositReference);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setWallet(wallet);

//        Wallet wallet =
//                walletRepo.findByUserAccount(userAccount)
//                        .orElseThrow();

        transactionRepo.save(transaction);

        log.info("Deposit {} completed successfully", depositReference);

        eventPublisher.publishEvent(new DepositAlertEvent(
                userAccount.getEmail(),
                transaction.getId(),
                WalletService.maskAccountNumber(wallet.getAccountNumber()),
                transaction.getAmount(),
                wallet.getCurrency(),
                transaction.getTransactionReference(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                wallet.getBalance()
        ));

        return new DepositResponse(
                deposit.getId(),
                userAccount.getWallet().getId(),
                depositReference,
                deposit.getAmount(),
                deposit.getTransactionStatus(),
                deposit.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public DepositResponse getDepositById(UserAccount userAccount, UUID depositId) {
        Deposit deposit = depositRepo.findByIdAndToWallet(depositId, userAccount.getWallet())
                .orElseThrow(() -> new NotFound("Deposit not found"));

        return new DepositResponse(
                deposit.getId(),
                deposit.getToWallet().getId(),
                deposit.getTransactionReference(),
                deposit.getAmount(),
                deposit.getTransactionStatus(),
                deposit.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<DepositResponse> getAllDeposits(UserAccount userAccount, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 10),
                Sort.by("createdAt").descending()
        );

        Page<Deposit> page = depositRepo.findByToWallet(userAccount.getWallet(), pageRequest);

        return page.map(
                deposit -> new DepositResponse(
                        deposit.getId(),
                        deposit.getToWallet().getId(),
                        deposit.getTransactionReference(),
                        deposit.getAmount(),
                        deposit.getTransactionStatus(),
                        deposit.getCreatedAt()
                )
        );
    }
}

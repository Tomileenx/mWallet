package com.example.naijaWallet.transfer;


import com.example.naijaWallet.enumTypes.BalanceType;
import com.example.naijaWallet.enumTypes.TransactionStatus;
import com.example.naijaWallet.enumTypes.TransactionType;
import com.example.naijaWallet.exception.BadRequest;
import com.example.naijaWallet.exception.NotFound;
import com.example.naijaWallet.idempotency.IdempotencyRecord;
import com.example.naijaWallet.idempotency.IdempotencyRecordRepo;
import com.example.naijaWallet.transaction.Transaction;
import com.example.naijaWallet.transaction.TransactionRepo;
import com.example.naijaWallet.transaction.TransactionService;
import com.example.naijaWallet.userAccount.UserAccount;
import com.example.naijaWallet.userEvent.CreditAlertEvent;
import com.example.naijaWallet.userEvent.DebitAlertEvent;
import com.example.naijaWallet.wallet.Wallet;
import com.example.naijaWallet.wallet.WalletRepo;
import com.example.naijaWallet.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepo transferRepo;
    private final TransactionRepo transactionRepo;
    private final WalletRepo walletRepo;
    private final IdempotencyRecordRepo idempotencyKeyRepo;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional
    public TransferResponse transferMoney(
            UserAccount userAccount,
            String idempotencyKey,
            TransferRequest request
    ) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequest("Transfer amount must be greater than zero");
        }

        String transferReference  = TransactionService.generateTransactionReference();

        try {
            IdempotencyRecord key = new IdempotencyRecord();
            key.setIdempotencyKey(idempotencyKey);
            key.setUserAccount(userAccount);
            key.setTransactionReference(transferReference);
            key.setTransactionType(TransactionType.TRANSFER_OUT);
            key.setCreatedAt(LocalDateTime.now());

            idempotencyKeyRepo.save(key);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequest("Duplicate deposit request");
        }

        Wallet sender = userAccount.getWallet();

        Wallet receiver = walletRepo.findById(request.toWalletId())
                .orElseThrow(() -> new NotFound("Receiver wallet not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new BadRequest("Cannot transfer to the same wallet");
        }

        int debitRows = walletRepo.debitWallet(userAccount, request.amount());

        if (debitRows == 0) {
            throw new BadRequest("Insufficient Balance");
        }

        int creditRows = walletRepo.creditWallet(receiver.getId(), request.amount());

        if (creditRows == 0) {
            throw new BadRequest("Wallet not found");
        }

        //  ensure correct balance in memory
        BigDecimal senderBalance =
                walletRepo.findBalanceById(sender.getId());

        BigDecimal receiverBalance =
                walletRepo.findBalanceById(receiver.getId());


        log.info("Initiating transfer of {} from wallet {} to {}",
                request.amount(),
                sender.getId(),
                receiver.getId()
        );

        Transfer transfer = new Transfer();
        transfer.setFromWallet(sender);
        transfer.setToWallet(receiver);
        transfer.setAmount(request.amount());
        transfer.setTransactionReference(transferReference);
        transfer.setTransactionStatus(TransactionStatus.SUCCESS);
        transfer.setCreatedAt(LocalDateTime.now());

        transferRepo.save(transfer);

        Transaction debitEntry = new Transaction();
        debitEntry.setWallet(sender);
        debitEntry.setTransactionType(TransactionType.TRANSFER_OUT);
        debitEntry.setBalanceType(BalanceType.DEBIT);
        debitEntry.setAmount(request.amount());
        debitEntry.setTransactionReference(transferReference);
        debitEntry.setStatus(TransactionStatus.SUCCESS);
        debitEntry.setCreatedAt(LocalDateTime.now());

        Transaction creditEntry = new Transaction();
        creditEntry.setWallet(receiver);
        creditEntry.setTransactionType(TransactionType.TRANSFER_IN);
        creditEntry.setBalanceType(BalanceType.CREDIT);
        creditEntry.setAmount(request.amount());
        creditEntry.setTransactionReference(transferReference);
        creditEntry.setStatus(TransactionStatus.SUCCESS);
        creditEntry.setCreatedAt(LocalDateTime.now());

        transactionRepo.saveAll(
                List.of(debitEntry, creditEntry)
        );

        log.info(
                "Transfer completed successfully {}",
                transferReference
        );

        log.info("Debit event balance: {}", senderBalance);
        eventPublisher.publishEvent(new DebitAlertEvent(
                userAccount.getEmail(),
                debitEntry.getId(),
                WalletService.maskAccountNumber(sender.getAccountNumber()),
                debitEntry.getAmount(),
                sender.getCurrency(),
                receiver.getUserAccount().getFullName(),
                debitEntry.getTransactionReference(),
                debitEntry.getStatus(),
                debitEntry.getCreatedAt(),
                senderBalance
        ));


        log.info("Credit event balance: {}", receiverBalance);
        eventPublisher.publishEvent(new CreditAlertEvent(
                receiver.getUserAccount().getEmail(),
                creditEntry.getId(),
                WalletService.maskAccountNumber(receiver.getAccountNumber()),
                creditEntry.getAmount(),
                receiver.getCurrency(),
                creditEntry.getTransactionReference(),
                creditEntry.getCreatedAt(),
                receiverBalance
        ));

        return new TransferResponse(
                transfer.getId(),
                sender.getId(),
                receiver.getId(),
                transfer.getAmount(),
                transferReference,
                "TRANSFER_OUT",
                transfer.getTransactionStatus(),
                transfer.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public TransferResponse getTransferId(UserAccount userAccount, UUID transferId) {

        Wallet wallet = userAccount.getWallet();

        Transfer transfer = transferRepo.findUserTransfer(
                transferId,
                wallet
        ).orElseThrow(() -> new NotFound("Transfer not found"));

        String direction =
                transfer.getFromWallet().getId().equals(wallet.getId())
                        ? "TRANSFER_OUT"
                        : "TRANSFER_IN";


        return new TransferResponse(
                transfer.getId(),
                transfer.getFromWallet().getId(),
                transfer.getToWallet().getId(),
                transfer.getAmount(),
                transfer.getTransactionReference(),
                direction,
                transfer.getTransactionStatus(),
                transfer.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<TransferResponse> getAllTransfers(UserAccount userAccount, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 10),
                Sort.by("createdAt").descending()
        );

        Wallet wallet = userAccount.getWallet();

        Page<Transfer> page = transferRepo.findByFromWalletOrToWallet(
                wallet,
                wallet,
                pageRequest
        );

        return page.map(transfer -> {

            String direction =
                    transfer.getFromWallet().getId().equals(wallet.getId())
                            ? "TRANSFER_OUT"
                            : "TRANSFER_IN";

            return new TransferResponse(
                    transfer.getId(),
                    transfer.getFromWallet().getId(),
                    transfer.getToWallet().getId(),
                    transfer.getAmount(),
                    transfer.getTransactionReference(),
                    direction,
                    transfer.getTransactionStatus(),
                    transfer.getCreatedAt()
            );
        });
    }
}

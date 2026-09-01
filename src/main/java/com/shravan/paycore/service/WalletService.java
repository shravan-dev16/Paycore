package com.shravan.paycore.service;

import com.shravan.paycore.dto.*;
import com.shravan.paycore.entity.IdempotencyRecord;
import com.shravan.paycore.entity.LedgerEntry;
import com.shravan.paycore.entity.Transaction;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.entity.Wallet;
import com.shravan.paycore.enums.LedgerEntryType;
import com.shravan.paycore.enums.TransactionStatus;
import com.shravan.paycore.enums.TransactionType;
import com.shravan.paycore.exception.DuplicateIdempotencyKeyException;
import com.shravan.paycore.exception.InsufficientBalanceException;
import com.shravan.paycore.exception.UserNotFoundException;
import com.shravan.paycore.repository.IdempotencyRecordRepository;
import com.shravan.paycore.repository.LedgerEntryRepository;
import com.shravan.paycore.repository.TransactionRepository;
import com.shravan.paycore.repository.UserRepository;
import com.shravan.paycore.repository.WalletRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public WalletService(
            WalletRepository walletRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            IdempotencyRecordRepository idempotencyRecordRepository,
            LedgerEntryRepository ledgerEntryRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.authenticatedUserService = authenticatedUserService;
    }


    // =========================
    // GET WALLET
    // =========================

    @Transactional
    public WalletResponse getWalletByUserId(Long userId) {

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        if (!authenticatedUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this wallet"
            );
        }

        Wallet wallet = walletRepository
                .findByUser(authenticatedUser)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );
    }


    // =========================
    // DEPOSIT
    // =========================

    @Transactional
    public WalletResponse deposit(
            Long userId,
            DepositRequest request
    ) {

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        if (!authenticatedUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this wallet"
            );
        }

        User user = authenticatedUser;

        Wallet wallet = walletRepository
                .findByUserForUpdate(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        // 1. Update wallet balance

        wallet.setBalance(
                wallet.getBalance()
                        .add(request.getAmount())
        );

        walletRepository.save(wallet);


        // 2. Create transaction

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReceiver(user);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);


        // 3. Create CREDIT ledger entry

        LedgerEntry ledgerEntry = new LedgerEntry();

        ledgerEntry.setTransaction(transaction);
        ledgerEntry.setWallet(wallet);
        ledgerEntry.setType(LedgerEntryType.CREDIT);
        ledgerEntry.setAmount(request.getAmount());
        ledgerEntry.setCreatedAt(LocalDateTime.now());

        ledgerEntryRepository.save(ledgerEntry);


        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );
    }


    // =========================
    // WITHDRAW
    // =========================

    @Transactional
    public WalletResponse withdraw(
            Long userId,
            WithdrawRequest request
    ) {

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        if (!authenticatedUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this wallet"
            );
        }

        User user = authenticatedUser;

        Wallet wallet = walletRepository
                .findByUserForUpdate(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));


        // 1. Check balance

        if (wallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }


        // 2. Update wallet balance

        wallet.setBalance(
                wallet.getBalance()
                        .subtract(request.getAmount())
        );

        walletRepository.save(wallet);


        // 3. Create transaction

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setSender(user);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);


        // 4. Create DEBIT ledger entry

        LedgerEntry ledgerEntry = new LedgerEntry();

        ledgerEntry.setTransaction(transaction);
        ledgerEntry.setWallet(wallet);
        ledgerEntry.setType(LedgerEntryType.DEBIT);
        ledgerEntry.setAmount(request.getAmount());
        ledgerEntry.setCreatedAt(LocalDateTime.now());

        ledgerEntryRepository.save(ledgerEntry);


        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );
    }


    // =========================
    // TRANSFER
    // =========================

    @Transactional
    public WalletResponse transfer(
            Long senderId,
            TransferRequest request,
            String idempotencyKey
    ) {

        // 1. Check idempotency

        Optional<IdempotencyRecord> existingRecord =
                idempotencyRecordRepository
                        .findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {

            throw new DuplicateIdempotencyKeyException(
                    "Request with this Idempotency-Key has already been processed"
            );
        }


        // 2. Get authenticated sender

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        if (!authenticatedUser.getId().equals(senderId)) {
            throw new AccessDeniedException(
                    "You are not authorized to transfer from this wallet"
            );
        }

        User sender = authenticatedUser;


        // 3. Find receiver

        User receiver = userRepository
                .findById(request.getReceiverId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Receiver not found"
                        ));


        // 4. Lock both wallets in consistent order

        Wallet firstWallet;
        Wallet secondWallet;

        if (sender.getId() < receiver.getId()) {

            firstWallet = walletRepository
                    .findByUserForUpdate(sender)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Sender wallet not found"
                            ));

            secondWallet = walletRepository
                    .findByUserForUpdate(receiver)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Receiver wallet not found"
                            ));

        } else {

            firstWallet = walletRepository
                    .findByUserForUpdate(receiver)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Receiver wallet not found"
                            ));

            secondWallet = walletRepository
                    .findByUserForUpdate(sender)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Sender wallet not found"
                            ));
        }


        // 5. Identify wallets

        Wallet senderWallet =
                sender.getId() < receiver.getId()
                        ? firstWallet
                        : secondWallet;

        Wallet receiverWallet =
                sender.getId() < receiver.getId()
                        ? secondWallet
                        : firstWallet;


        // 6. Check balance

        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }


        // 7. Create transaction as PENDING

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);

        transaction.changeStatus(
                TransactionStatus.PENDING
        );

        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        transaction.setCreatedAt(
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);


        // 8. Debit sender

        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );


        // 9. Credit receiver

        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );


        // 10. Save wallets

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);


        // 11. Create sender DEBIT ledger entry

        LedgerEntry senderLedger = new LedgerEntry();

        senderLedger.setTransaction(transaction);
        senderLedger.setWallet(senderWallet);
        senderLedger.setType(LedgerEntryType.DEBIT);
        senderLedger.setAmount(request.getAmount());
        senderLedger.setCreatedAt(LocalDateTime.now());

        ledgerEntryRepository.save(senderLedger);


        // 12. Create receiver CREDIT ledger entry

        LedgerEntry receiverLedger = new LedgerEntry();

        receiverLedger.setTransaction(transaction);
        receiverLedger.setWallet(receiverWallet);
        receiverLedger.setType(LedgerEntryType.CREDIT);
        receiverLedger.setAmount(request.getAmount());
        receiverLedger.setCreatedAt(LocalDateTime.now());

        ledgerEntryRepository.save(receiverLedger);


        // 13. Transfer succeeded
        //     PENDING -> COMPLETED

        transaction.changeStatus(
                TransactionStatus.COMPLETED
        );

        transactionRepository.save(transaction);


        // 14. Store idempotency record

        IdempotencyRecord record =
                new IdempotencyRecord();

        record.setIdempotencyKey(idempotencyKey);
        record.setTransactionId(transaction.getId());
        record.setCreatedAt(LocalDateTime.now());

        idempotencyRecordRepository.save(record);


        // 15. Return sender wallet

        return new WalletResponse(
                senderWallet.getId(),
                senderWallet.getBalance()
        );
    }
    @Transactional(readOnly = true)
    public WalletConsistencyResponse checkWalletConsistency(Long userId) {

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        if (!authenticatedUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this wallet"
            );
        }

        Wallet wallet = walletRepository
                .findByUser(authenticatedUser)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        BigDecimal ledgerBalance =
                ledgerEntryRepository.calculateBalance(
                        wallet.getId(),
                        LedgerEntryType.CREDIT
                );

        boolean consistent =
                wallet.getBalance().compareTo(ledgerBalance) == 0;

        return new WalletConsistencyResponse(
                wallet.getId(),
                wallet.getBalance(),
                ledgerBalance,
                consistent
        );
    }
}
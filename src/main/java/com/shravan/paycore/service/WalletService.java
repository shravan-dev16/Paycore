package com.shravan.paycore.service;
import com.shravan.paycore.exception.DuplicateIdempotencyKeyException;
import com.shravan.paycore.dto.DepositRequest;
import com.shravan.paycore.dto.TransferRequest;
import com.shravan.paycore.dto.WalletResponse;
import com.shravan.paycore.dto.WithdrawRequest;
import com.shravan.paycore.entity.IdempotencyRecord;
import com.shravan.paycore.entity.Transaction;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.entity.Wallet;
import com.shravan.paycore.enums.TransactionStatus;
import com.shravan.paycore.enums.TransactionType;
import com.shravan.paycore.exception.InsufficientBalanceException;
import com.shravan.paycore.exception.UserNotFoundException;
import com.shravan.paycore.repository.IdempotencyRecordRepository;
import com.shravan.paycore.repository.TransactionRepository;
import com.shravan.paycore.repository.UserRepository;
import com.shravan.paycore.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public WalletService(
            WalletRepository walletRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            IdempotencyRecordRepository idempotencyRecordRepository
    ) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    // =========================
    // GET WALLET
    // =========================

    @Transactional
    public WalletResponse getWalletByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
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

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUserForUpdate(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        wallet.setBalance(
                wallet.getBalance()
                        .add(request.getAmount())
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReceiver(user);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

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

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUserForUpdate(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        if (wallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }

        wallet.setBalance(
                wallet.getBalance()
                        .subtract(request.getAmount())
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setSender(user);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

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

        // 1. Check whether this request
        //    was already processed

        Optional<IdempotencyRecord> existingRecord =
                idempotencyRecordRepository
                        .findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {

            throw new DuplicateIdempotencyKeyException(
                    "Request with this Idempotency-Key has already been processed"
            );
        }


        // 2. Find sender

        User sender = userRepository.findById(senderId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Sender not found"
                        ));


        // 3. Find receiver

        User receiver = userRepository
                .findById(request.getReceiverId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Receiver not found"
                        ));


        // 4. Lock both wallets in a consistent order.
        //
        //    This prevents deadlocks when:
        //
        //    User 7 -> User 6
        //
        //    happens at the same time as:
        //
        //    User 6 -> User 7

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


        // 5. Identify sender and receiver wallets

        Wallet senderWallet =
                sender.getId() < receiver.getId()
                        ? firstWallet
                        : secondWallet;

        Wallet receiverWallet =
                sender.getId() < receiver.getId()
                        ? secondWallet
                        : firstWallet;


        // 6. Check sender balance

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


        // 10. Save both wallets

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);


        // 11. Transfer succeeded
        //     PENDING -> COMPLETED

        transaction.changeStatus(
                TransactionStatus.COMPLETED
        );

        transactionRepository.save(transaction);


        // 12. Store idempotency record

        IdempotencyRecord record =
                new IdempotencyRecord();

        record.setIdempotencyKey(idempotencyKey);
        record.setTransactionId(transaction.getId());
        record.setCreatedAt(LocalDateTime.now());

        idempotencyRecordRepository.save(record);


        // 13. Return sender's updated wallet

        return new WalletResponse(
                senderWallet.getId(),
                senderWallet.getBalance()
        );
    }
}
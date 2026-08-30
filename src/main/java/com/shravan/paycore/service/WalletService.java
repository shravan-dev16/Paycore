package com.shravan.paycore.service;

import com.shravan.paycore.dto.DepositRequest;
import com.shravan.paycore.dto.TransferRequest;
import com.shravan.paycore.dto.WalletResponse;
import com.shravan.paycore.dto.WithdrawRequest;
import com.shravan.paycore.entity.Transaction;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.entity.Wallet;
import com.shravan.paycore.enums.TransactionStatus;
import com.shravan.paycore.enums.TransactionType;
import com.shravan.paycore.exception.InsufficientBalanceException;
import com.shravan.paycore.exception.UserNotFoundException;
import com.shravan.paycore.repository.TransactionRepository;
import com.shravan.paycore.repository.UserRepository;
import com.shravan.paycore.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    // =========================
    // GET WALLET
    // =========================

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
    public WalletResponse deposit(Long userId, DepositRequest request) {

        System.out.println(">>> DEPOSIT METHOD STARTED");

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        System.out.println(">>> USER FOUND: " + user.getEmail());

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        System.out.println(">>> WALLET FOUND: " + wallet.getBalance());

        wallet.setBalance(
                wallet.getBalance().add(request.getAmount())
        );

        System.out.println(">>> NEW BALANCE: " + wallet.getBalance());

        walletRepository.save(wallet);

        System.out.println(">>> WALLET SAVED");

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReceiver(user);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        System.out.println(">>> TRANSACTION SAVED");

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );
    }


    // =========================
    // WITHDRAW
    // =========================

    @Transactional
    public WalletResponse withdraw(Long userId, WithdrawRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }

        wallet.setBalance(
                wallet.getBalance().subtract(request.getAmount())
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
    public WalletResponse transfer(Long senderId, TransferRequest request) {

        // 1. Find sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() ->
                        new UserNotFoundException("Sender not found"));

        // 2. Find receiver
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() ->
                        new UserNotFoundException("Receiver not found"));

        // 3. Find sender wallet
        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() ->
                        new RuntimeException("Sender wallet not found"));

        // 4. Find receiver wallet
        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() ->
                        new RuntimeException("Receiver wallet not found"));

        // 5. Check sender balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }

        // 6. Create transaction as PENDING
        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.changeStatus(TransactionStatus.PENDING);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        // 7. Debit sender
        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );

        // 8. Credit receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        // 9. Save both wallets
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 10. Transfer succeeded
        // PENDING -> COMPLETED
        transaction.changeStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(transaction);

        // 11. Return sender's updated wallet
        return new WalletResponse(
                senderWallet.getId(),
                senderWallet.getBalance()
        );
    }
}
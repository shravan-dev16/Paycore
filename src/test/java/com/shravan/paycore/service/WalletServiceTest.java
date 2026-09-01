package com.shravan.paycore.service;

import com.shravan.paycore.dto.TransferRequest;
import com.shravan.paycore.dto.WithdrawRequest;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.entity.Wallet;
import com.shravan.paycore.exception.InsufficientBalanceException;
import com.shravan.paycore.repository.IdempotencyRecordRepository;
import com.shravan.paycore.repository.LedgerEntryRepository;
import com.shravan.paycore.repository.TransactionRepository;
import com.shravan.paycore.repository.UserRepository;
import com.shravan.paycore.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    private WalletService walletService;

    @BeforeEach
    void setUp() {

        walletService = new WalletService(
                walletRepository,
                userRepository,
                transactionRepository,
                idempotencyRecordRepository,
                ledgerEntryRepository,
                authenticatedUserService
        );
    }

    @Test
    void withdraw_shouldRejectWhenBalanceIsInsufficient() {

        User user = new User();
        user.setId(7L);

        Wallet wallet = new Wallet();

        wallet.setBalance(
                new BigDecimal("50.00")
        );

        wallet.setUser(user);

        WithdrawRequest request = new WithdrawRequest();

        request.setAmount(
                new BigDecimal("100.00")
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(walletRepository.findByUserForUpdate(user))
                .thenReturn(Optional.of(wallet));

        assertThrows(
                InsufficientBalanceException.class,
                () -> walletService.withdraw(7L, request)
        );

        verify(walletRepository, never())
                .save(any(Wallet.class));

        verify(transactionRepository, never())
                .save(any());

        verify(ledgerEntryRepository, never())
                .save(any());
    }
    @Test
    void withdraw_shouldSuccessfullyWithdrawMoney() {

        User user = new User();
        user.setId(7L);

        Wallet wallet = new Wallet();

        wallet.setBalance(
                new BigDecimal("500.00")
        );

        wallet.setUser(user);

        WithdrawRequest request = new WithdrawRequest();

        request.setAmount(
                new BigDecimal("100.00")
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(walletRepository.findByUserForUpdate(user))
                .thenReturn(Optional.of(wallet));

        walletService.withdraw(7L, request);

        assert wallet.getBalance()
                .compareTo(new BigDecimal("400.00")) == 0;

        verify(walletRepository)
                .save(wallet);

        verify(transactionRepository)
                .save(any());

        verify(ledgerEntryRepository)
                .save(any());
    }
    @Test
    void transfer_shouldMoveMoneyBetweenWallets() {

        User sender = new User();
        sender.setId(7L);

        User receiver = new User();
        receiver.setId(8L);

        Wallet senderWallet = new Wallet();
        senderWallet.setUser(sender);
        senderWallet.setBalance(
                new BigDecimal("500.00")
        );

        Wallet receiverWallet = new Wallet();
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(
                new BigDecimal("100.00")
        );

        TransferRequest request = new TransferRequest();
        request.setReceiverId(8L);
        request.setAmount(
                new BigDecimal("100.00")
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(sender);

        when(idempotencyRecordRepository
                .findByIdempotencyKey("test-key"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(8L))
                .thenReturn(Optional.of(receiver));

        when(walletRepository.findByUserForUpdate(sender))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUserForUpdate(receiver))
                .thenReturn(Optional.of(receiverWallet));

        walletService.transfer(
                7L,
                request,
                "test-key"
        );

        assert senderWallet.getBalance()
                .compareTo(new BigDecimal("400.00")) == 0;

        assert receiverWallet.getBalance()
                .compareTo(new BigDecimal("200.00")) == 0;

        verify(walletRepository)
                .save(senderWallet);

        verify(walletRepository)
                .save(receiverWallet);

        verify(transactionRepository, atLeastOnce())
                .save(any());

        verify(ledgerEntryRepository, times(2))
                .save(any());

        verify(idempotencyRecordRepository)
                .save(any());
    }
    @Test
    void transfer_shouldRejectWhenSenderHasInsufficientBalance() {

        User sender = new User();
        sender.setId(7L);

        User receiver = new User();
        receiver.setId(8L);

        Wallet senderWallet = new Wallet();
        senderWallet.setUser(sender);
        senderWallet.setBalance(
                new BigDecimal("50.00")
        );

        Wallet receiverWallet = new Wallet();
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(
                new BigDecimal("100.00")
        );

        TransferRequest request = new TransferRequest();
        request.setReceiverId(8L);
        request.setAmount(
                new BigDecimal("100.00")
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(sender);

        when(idempotencyRecordRepository
                .findByIdempotencyKey("insufficient-test"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(8L))
                .thenReturn(Optional.of(receiver));

        when(walletRepository.findByUserForUpdate(sender))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUserForUpdate(receiver))
                .thenReturn(Optional.of(receiverWallet));

        assertThrows(
                InsufficientBalanceException.class,
                () -> walletService.transfer(
                        7L,
                        request,
                        "insufficient-test"
                )
        );

        assert senderWallet.getBalance()
                .compareTo(new BigDecimal("50.00")) == 0;

        assert receiverWallet.getBalance()
                .compareTo(new BigDecimal("100.00")) == 0;

        verify(transactionRepository, never())
                .save(any());

        verify(ledgerEntryRepository, never())
                .save(any());

        verify(idempotencyRecordRepository, never())
                .save(any());
    }
}
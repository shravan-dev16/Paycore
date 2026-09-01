package com.shravan.paycore.controller;

import com.shravan.paycore.dto.*;
import com.shravan.paycore.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @GetMapping("/{userId}/consistency")
    public ResponseEntity<WalletConsistencyResponse> checkWalletConsistency(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                walletService.checkWalletConsistency(userId)
        );
    }
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // =========================
    // GET WALLET
    // =========================

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWallet(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                walletService.getWalletByUserId(userId)
        );
    }


    // =========================
    // DEPOSIT
    // =========================

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @PathVariable Long userId,
            @Valid @RequestBody DepositRequest request
    ) {
        return ResponseEntity.ok(
                walletService.deposit(userId, request)
        );
    }


    // =========================
    // WITHDRAW
    // =========================

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long userId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        return ResponseEntity.ok(
                walletService.withdraw(userId, request)
        );
    }


    // =========================
    // TRANSFER
    // =========================

    @PostMapping("/{userId}/transfer")
    public ResponseEntity<WalletResponse> transfer(
            @PathVariable Long userId,

            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @Valid @RequestBody TransferRequest request
    ) {
        return ResponseEntity.ok(
                walletService.transfer(
                        userId,
                        request,
                        idempotencyKey
                )
        );
    }
}
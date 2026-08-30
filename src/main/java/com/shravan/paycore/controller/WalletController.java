package com.shravan.paycore.controller;

import com.shravan.paycore.dto.TransferRequest;
import com.shravan.paycore.dto.WalletResponse;
import com.shravan.paycore.dto.WithdrawRequest;
import com.shravan.paycore.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shravan.paycore.dto.DepositRequest;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWallet(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                walletService.getWalletByUserId(userId)
        );
    }
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @PathVariable Long userId,
            @Valid @RequestBody DepositRequest request
    ) {
        return ResponseEntity.ok(
                walletService.deposit(userId, request)
        );
    }
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long userId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        return ResponseEntity.ok(
                walletService.withdraw(userId, request)
        );
    }
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<WalletResponse> transfer(
            @PathVariable Long userId,
            @Valid @RequestBody TransferRequest request
    ) {
        return ResponseEntity.ok(
                walletService.transfer(userId, request)
        );
    }
}
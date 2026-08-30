package com.shravan.paycore.controller;

import com.shravan.paycore.dto.TransactionResponse;
import com.shravan.paycore.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByUserId(
            @PathVariable Long userId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByUserId(
                        userId,
                        pageable
                )
        );
    }
}
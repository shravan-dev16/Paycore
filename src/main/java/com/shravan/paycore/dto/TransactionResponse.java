package com.shravan.paycore.dto;

import com.shravan.paycore.enums.TransactionStatus;
import com.shravan.paycore.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionResponse {

    private UUID id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;

    public TransactionResponse(
            UUID id,
            BigDecimal amount,
            TransactionType type,
            TransactionStatus status,
            Long senderId,
            Long receiverId,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
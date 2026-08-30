package com.shravan.paycore.entity;

import com.shravan.paycore.enums.TransactionStatus;
import com.shravan.paycore.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void changeStatus(TransactionStatus newStatus) {

        if (this.status == null) {
            this.status = newStatus;
            return;
        }

        boolean validTransition =
                (this.status == TransactionStatus.PENDING &&
                        (newStatus == TransactionStatus.COMPLETED ||
                                newStatus == TransactionStatus.FAILED))

                        ||

                        (this.status == TransactionStatus.COMPLETED &&
                                newStatus == TransactionStatus.REVERSED);

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid transaction status transition: "
                            + this.status + " -> " + newStatus
            );
        }

        this.status = newStatus;
    }
}
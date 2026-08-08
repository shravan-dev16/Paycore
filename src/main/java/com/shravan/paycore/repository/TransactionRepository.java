package com.shravan.paycore.repository;

import com.shravan.paycore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

}
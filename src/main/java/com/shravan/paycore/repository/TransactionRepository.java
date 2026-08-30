package com.shravan.paycore.repository;

import com.shravan.paycore.entity.Transaction;
import com.shravan.paycore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findBySenderOrReceiver(
            User sender,
            User receiver,
            Pageable pageable
    );
}
package com.shravan.paycore.service;

import com.shravan.paycore.dto.TransactionResponse;
import com.shravan.paycore.entity.Transaction;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.exception.UserNotFoundException;
import com.shravan.paycore.repository.TransactionRepository;
import com.shravan.paycore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionResponse> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getSender() != null
                        ? transaction.getSender().getId()
                        : null,
                transaction.getReceiver() != null
                        ? transaction.getReceiver().getId()
                        : null,
                transaction.getCreatedAt()
        );
    }

    public Page<TransactionResponse> getTransactionsByUserId(
            Long userId,
            Pageable pageable
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return transactionRepository
                .findBySenderOrReceiver(user, user, pageable)
                .map(this::toResponse);
    }
}
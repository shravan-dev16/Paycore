package com.shravan.paycore.repository;

import com.shravan.paycore.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository
        extends JpaRepository<Wallet, Long> {

}
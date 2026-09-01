package com.shravan.paycore.repository;

import com.shravan.paycore.entity.LedgerEntry;
import com.shravan.paycore.enums.LedgerEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerEntryRepository
        extends JpaRepository<LedgerEntry, UUID> {

    @Query("""
            SELECT COALESCE(SUM(
                CASE
                    WHEN l.type = :credit THEN l.amount
                    ELSE -l.amount
                END
            ), 0)
            FROM LedgerEntry l
            WHERE l.wallet.id = :walletId
            """)
    BigDecimal calculateBalance(
            @Param("walletId") Long walletId,
            @Param("credit") LedgerEntryType credit
    );
}
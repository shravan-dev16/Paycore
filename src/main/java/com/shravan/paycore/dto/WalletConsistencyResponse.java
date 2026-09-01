package com.shravan.paycore.dto;

import java.math.BigDecimal;

public class WalletConsistencyResponse {

    private Long walletId;
    private BigDecimal walletBalance;
    private BigDecimal ledgerBalance;
    private boolean consistent;

    public WalletConsistencyResponse(
            Long walletId,
            BigDecimal walletBalance,
            BigDecimal ledgerBalance,
            boolean consistent
    ) {
        this.walletId = walletId;
        this.walletBalance = walletBalance;
        this.ledgerBalance = ledgerBalance;
        this.consistent = consistent;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public BigDecimal getLedgerBalance() {
        return ledgerBalance;
    }

    public boolean isConsistent() {
        return consistent;
    }
}
package com.shravan.paycore.service;

import com.shravan.paycore.entity.Wallet;
import com.shravan.paycore.enums.LedgerEntryType;
import com.shravan.paycore.repository.LedgerEntryRepository;
import com.shravan.paycore.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReconciliationService {

    private static final Logger logger =
            LoggerFactory.getLogger(ReconciliationService.class);

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public ReconciliationService(
            WalletRepository walletRepository,
            LedgerEntryRepository ledgerEntryRepository
    ) {
        this.walletRepository = walletRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional(readOnly = true)
    public void reconcileAllWallets() {

        List<Wallet> wallets = walletRepository.findAll();

        logger.info(
                "Starting wallet reconciliation. Wallets found: {}",
                wallets.size()
        );

        int inconsistencies = 0;

        for (Wallet wallet : wallets) {

            BigDecimal walletBalance =
                    wallet.getBalance();

            BigDecimal ledgerBalance =
                    ledgerEntryRepository.calculateBalance(
                            wallet.getId(),
                            LedgerEntryType.CREDIT
                    );

            if (walletBalance.compareTo(ledgerBalance) != 0) {

                inconsistencies++;

                logger.error(
                        "WALLET RECONCILIATION FAILED | walletId={} | walletBalance={} | ledgerBalance={}",
                        wallet.getId(),
                        walletBalance,
                        ledgerBalance
                );

            } else {

                logger.info(
                        "Wallet reconciliation successful | walletId={} | balance={}",
                        wallet.getId(),
                        walletBalance
                );
            }
        }

        logger.info(
                "Wallet reconciliation completed. Inconsistencies: {}",
                inconsistencies
        );
    }
}
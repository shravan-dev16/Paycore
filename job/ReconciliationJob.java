package com.shravan.paycore.job;

import com.shravan.paycore.service.ReconciliationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationJob {

    private final ReconciliationService reconciliationService;

    public ReconciliationJob(
            ReconciliationService reconciliationService
    ) {
        this.reconciliationService = reconciliationService;
    }

    @Scheduled(fixedRate = 60000)
    public void reconcileWallets() {

        reconciliationService.reconcileAllWallets();
    }
}
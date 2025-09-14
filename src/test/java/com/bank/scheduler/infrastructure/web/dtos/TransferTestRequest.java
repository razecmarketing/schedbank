package com.bank.scheduler.infrastructure.web.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferTestRequest {
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal amount;
    private LocalDate transferDate;

    public TransferTestRequest(String sourceAccount, String targetAccount, BigDecimal amount, LocalDate transferDate) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.transferDate = transferDate;
    }

    public String getSourceAccount() { return sourceAccount; }
    public String getTargetAccount() { return targetAccount; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getTransferDate() { return transferDate; }
}

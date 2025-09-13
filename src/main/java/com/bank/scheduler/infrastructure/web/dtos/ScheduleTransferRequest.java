package com.bank.scheduler.infrastructure.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ScheduleTransferRequest {
    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Source account must be exactly 10 digits")
    private String sourceAccount;

    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Target account must be exactly 10 digits")
    private String targetAccount;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate transferDate;

    public String getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(String sourceAccount) { this.sourceAccount = sourceAccount; }

    public String getTargetAccount() { return targetAccount; }
    public void setTargetAccount(String targetAccount) { this.targetAccount = targetAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }
}

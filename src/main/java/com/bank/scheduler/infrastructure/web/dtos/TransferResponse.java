package com.bank.scheduler.infrastructure.web.dtos;

import com.bank.scheduler.domain.entities.Transfer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransferResponse {
    private UUID id;
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDate scheduleDate;
    private LocalDate transferDate;

    public static TransferResponse fromDomain(Transfer transfer) {
        TransferResponse response = new TransferResponse();
        response.setId(transfer.getId());
        response.setSourceAccount(transfer.getSourceAccount().getValue());
        response.setTargetAccount(transfer.getTargetAccount().getValue());
        response.setAmount(transfer.getAmount().getAmount());
        response.setFee(transfer.getFee().getAmount());
        response.setScheduleDate(transfer.getScheduleDate());
        response.setTransferDate(transfer.getTransferDate());
        return response;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(String sourceAccount) { this.sourceAccount = sourceAccount; }

    public String getTargetAccount() { return targetAccount; }
    public void setTargetAccount(String targetAccount) { this.targetAccount = targetAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }
}

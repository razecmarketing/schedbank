package com.bank.scheduler.domain.entities;

import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public final class Transfer {
    
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 10)
    private String sourceAccount;
    
    @Column(nullable = false, length = 10)
    private String targetAccount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fee;
    
    @Column(nullable = false)
    private LocalDate scheduleDate;
    
    @Column(nullable = false)
    private LocalDate transferDate;

    // Construtor padrão para JPA
    protected Transfer() {}

    private Transfer(
            UUID id,
            String sourceAccount,
            String targetAccount,
            BigDecimal amount,
            BigDecimal fee,
            LocalDate scheduleDate,
            LocalDate transferDate) {
        validateTransferDates(scheduleDate, transferDate);
        validateAccounts(sourceAccount, targetAccount);
        validateAmount(amount);
        
        this.id = id;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.fee = fee;
        this.scheduleDate = scheduleDate;
        this.transferDate = transferDate;
    }

    public static Transfer schedule(
            AccountNumber sourceAccount,
            AccountNumber targetAccount,
            Money amount,
            Money fee,
            LocalDate scheduleDate,
            LocalDate transferDate) {
        return new Transfer(
            UUID.randomUUID(),
            sourceAccount.getValue(),
            targetAccount.getValue(),
            amount.getAmount(),
            fee.getAmount(),
            scheduleDate,
            transferDate
        );
    }

    private void validateTransferDates(LocalDate scheduleDate, LocalDate transferDate) {
        validateDatesNotNull(scheduleDate, transferDate);
        validateTransferDateNotInPast(scheduleDate, transferDate);
    }

    private void validateDatesNotNull(LocalDate scheduleDate, LocalDate transferDate) {
        if (scheduleDate == null || transferDate == null) {
            throw new IllegalArgumentException("Schedule and transfer dates must be provided");
        }
    }

    private void validateTransferDateNotInPast(LocalDate scheduleDate, LocalDate transferDate) {
        if (transferDate.isBefore(scheduleDate)) {
            throw new DomainException.InvalidTransferDate("Transfer date cannot be before schedule date");
        }
    }

    private void validateAccounts(String sourceAccount, String targetAccount) {
        validateAccountsNotNull(sourceAccount, targetAccount);
        validateDifferentAccounts(sourceAccount, targetAccount);
    }

    private void validateAccountsNotNull(String sourceAccount, String targetAccount) {
        if (sourceAccount == null || targetAccount == null) {
            throw new IllegalArgumentException("Source and target accounts must be provided");
        }
    }

    private void validateDifferentAccounts(String sourceAccount, String targetAccount) {
        if (sourceAccount.equals(targetAccount)) {
            throw new DomainException.SameAccountNotAllowed("Cannot transfer to the same account");
        }
    }

    private void validateAmount(BigDecimal amount) {
        validateAmountNotNull(amount);
        validateAmountPositive(amount);
    }

    private void validateAmountNotNull(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Transfer amount must be provided");
        }
    }

    private void validateAmountPositive(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }
    }

    // Getters
    public UUID getId() { return id; }
    
    public AccountNumber getSourceAccount() { 
        return AccountNumber.of(sourceAccount); 
    }
    
    public AccountNumber getTargetAccount() { 
        return AccountNumber.of(targetAccount); 
    }
    
    public Money getAmount() { 
        return Money.of(amount); 
    }
    
    public Money getFee() { 
        return Money.of(fee); 
    }
    
    public LocalDate getScheduleDate() { return scheduleDate; }
    
    public LocalDate getTransferDate() { return transferDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(id, transfer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", sourceAccount='" + sourceAccount + '\'' +
                ", targetAccount='" + targetAccount + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", scheduleDate=" + scheduleDate +
                ", transferDate=" + transferDate +
                '}';
    }
}

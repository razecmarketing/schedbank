package com.bank.scheduler.domain.entities;

import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Transfer Domain Entity - Core Business Logic
 * 
 * Domain Invariants:
 * - sourceAccount != targetAccount (no self-transfers)
 * - amount > 0 (positive transfer amounts)
 * - transferDate >= scheduleDate (no past-dated execution)
 * - fee >= 0 (non-negative fees)
 * 
 * This entity contains pure business logic with no framework dependencies.
 */
public final class Transfer {
    
    // Pure domain fields - no technical framework concerns
    private final UUID id;
    private final AccountNumber sourceAccount;
    private final AccountNumber targetAccount;
    private final Money amount;
    private final Money fee;
    private final LocalDate scheduleDate;
    private final LocalDate transferDate;

    /**
     * Private constructor enforces business rules at creation time.
     * 
     * All domain constraints validated here following defensive programming.
     * Technical persistence concerns handled separately.
     */
    private Transfer(
            UUID id,
            AccountNumber sourceAccount,
            AccountNumber targetAccount,
            Money amount,
            Money fee,
            LocalDate scheduleDate,
            LocalDate transferDate) {
        
        // Validate all business rules before creating object
        validateBusinessInvariants(sourceAccount, targetAccount, amount, fee, scheduleDate, transferDate);
        
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

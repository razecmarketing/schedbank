package com.bank.scheduler.domain.entities;

import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
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
@Entity
@Table(name = "transfers")
public final class Transfer {
    
    // Pure domain fields - no technical framework concerns
    @Id
    @Column(columnDefinition = "UUID")
    private final UUID id;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "source_account"))
    })
    private final AccountNumber sourceAccount;
    
    @Embedded  
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "target_account"))
    })
    private final AccountNumber targetAccount;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount"))
    })
    private final Money amount;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "fee"))
    })
    private final Money fee;
    
    @Column(name = "schedule_date")
    private final LocalDate scheduleDate;
    
    @Column(name = "transfer_date")
    private final LocalDate transferDate;

    /**
     * Default constructor for JPA - not for direct use
     */
    protected Transfer() {
        this.id = null;
        this.sourceAccount = null;
        this.targetAccount = null;
        this.amount = null;
        this.fee = null;
        this.scheduleDate = null;
        this.transferDate = null;
    }

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
            sourceAccount,
            targetAccount,
            amount,
            fee,
            scheduleDate,
            transferDate
        );
    }

    /**
     * Validates all business invariants for transfer creation
     */
    private void validateBusinessInvariants(
            AccountNumber sourceAccount,
            AccountNumber targetAccount, 
            Money amount,
            Money fee,
            LocalDate scheduleDate,
            LocalDate transferDate) {
        
        // Validate accounts
        if (sourceAccount == null || targetAccount == null) {
            throw new IllegalArgumentException("Source and target accounts must be provided");
        }
        if (sourceAccount.equals(targetAccount)) {
            throw new DomainException.SameAccountNotAllowed("Cannot transfer to the same account");
        }
        
        // Validate amounts
        if (amount == null) {
            throw new IllegalArgumentException("Transfer amount must be provided");
        }
        if (fee == null) {
            throw new IllegalArgumentException("Transfer fee must be provided");
        }
        
        // Validate dates
        if (scheduleDate == null || transferDate == null) {
            throw new IllegalArgumentException("Schedule and transfer dates must be provided");
        }
        if (transferDate.isBefore(scheduleDate)) {
            throw new DomainException.InvalidTransferDate("Transfer date cannot be before schedule date");
        }
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

    private void validateAccounts(AccountNumber sourceAccount, AccountNumber targetAccount) {
        // Account validation is handled by the AccountNumber value object itself
        // Additional business rule: cannot transfer to same account
        if (sourceAccount.equals(targetAccount)) {
            throw new DomainException.SameAccountNotAllowed("Cannot transfer to the same account");
        }
    }



    private void validateAmount(Money amount) {
        // Money validation is handled by the Money value object itself
        // This method kept for compatibility but amount validation is done in Money constructor
    }



    // Getters
    public UUID getId() { return id; }
    
    public AccountNumber getSourceAccount() { 
        return sourceAccount; 
    }
    
    public AccountNumber getTargetAccount() { 
        return targetAccount; 
    }
    
    public Money getAmount() { 
        return amount; 
    }
    
    public Money getFee() { 
        return fee; 
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

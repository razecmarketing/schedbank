package com.bank.scheduler.infrastructure.persistence.entities;

import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Transfer Entity - Persistence Layer
 * 
 * Handles database mapping and persistence concerns.
 * Bridges between domain objects and database storage.
 * 
 * Performance optimization: Indexed fields for common queries.
 */
@Entity
@Table(name = "transfers", indexes = {
    @Index(name = "idx_transfer_date", columnList = "transferDate"),
    @Index(name = "idx_source_account", columnList = "sourceAccount"),
    @Index(name = "idx_schedule_date", columnList = "scheduleDate")
})
public class TransferEntity {
    
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

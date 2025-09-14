package com.bank.scheduler.domain.specifications;

import com.bank.scheduler.domain.entities.Transfer;
import java.time.LocalDate;

/**
 * Domain Specification following mathematical rigor principles.
 * 
 * Formal Specification:
 * ∀ transfer ∈ Domain:
 *   - sourceAccount ≠ targetAccount (distinctness)
 *   - amount > 0 (positive transfers)
 *   - transferDate ≥ scheduleDate (temporal consistency)
 *   - fee ≥ 0 (non-negative fees)
 * 
 * This class provides mathematical proofs and domain validation
 * following theorem-proving approach to software correctness.
 */
public final class TransferSpecification {
    
    private TransferSpecification() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates temporal consistency invariant.
     * 
     * Theorem: ∀ t ∈ Transfers, t.transferDate ≥ t.scheduleDate
     * Proof: Business rule requires execution cannot precede scheduling
     * 
     * @param scheduleDate when transfer was scheduled
     * @param transferDate when transfer will be executed
     * @return true if temporally consistent
     */
    public static boolean hasValidTemporalOrder(LocalDate scheduleDate, LocalDate transferDate) {
        if (scheduleDate == null || transferDate == null) {
            return false;
        }
        return !transferDate.isBefore(scheduleDate);
    }
    
    /**
     * Validates account distinctness invariant.
     * 
     * Theorem: ∀ t ∈ Transfers, t.sourceAccount ≠ t.targetAccount
     * Proof: Self-transfers violate business domain constraints
     * 
     * @param sourceAccount origin account
     * @param targetAccount destination account
     * @return true if accounts are distinct
     */
    public static boolean hasDistinctAccounts(String sourceAccount, String targetAccount) {
        if (sourceAccount == null || targetAccount == null) {
            return false;
        }
        return !sourceAccount.equals(targetAccount);
    }
    
    /**
     * Validates monetary positivity invariant.
     * 
     * Theorem: ∀ t ∈ Transfers, t.amount > 0 ∧ t.fee ≥ 0
     * Proof: Negative transfers violate business logic, fees are cost-based
     * 
     * @param amount transfer amount
     * @param fee calculated fee
     * @return true if monetary values are valid
     */
    public static boolean hasValidMonetaryValues(java.math.BigDecimal amount, java.math.BigDecimal fee) {
        if (amount == null || fee == null) {
            return false;
        }
        return amount.compareTo(java.math.BigDecimal.ZERO) > 0 
            && fee.compareTo(java.math.BigDecimal.ZERO) >= 0;
    }
}

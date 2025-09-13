package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Same Day Fee Policy - Immediate Transfer Fee Calculation
 * 
 * Business Rule:
 * fee(amount, days) = BASE_FEE + (amount * PERCENTAGE_RATE) where days = 0
 * 
 * Performance: O(1) - constant time calculation
 * Space: O(1) - no additional memory needed
 * 
 * Constraints:
 * - PERCENTAGE_RATE = 0.025 (2.5%)
 * - BASE_FEE = 3.00 BRL
 * - Applicable only when transfer happens same day as scheduling
 */
public final class SameDayFeePolicy implements FeePolicy {
    
    // Business constants for fee calculation
    private static final BigDecimal PERCENTAGE_RATE = new BigDecimal("0.025"); // 2.5%
    private static final Money BASE_FEE = Money.of(new BigDecimal("3.00"));    // Fixed fee
    private static final int SAME_DAY = 0;                                     // Zero days difference

    /**
     * Core fee calculation method.
     * 
     * Precondition: isApplicable(scheduleDate, transferDate) = true
     * Postcondition: fee = BASE_FEE + (transferAmount * PERCENTAGE_RATE)
     * 
     * Business logic:
     * 1. transferAmount >= 0 (Money constraint)  
     * 2. PERCENTAGE_RATE = 0.025 (business constant)
     * 3. BASE_FEE = 3.00 (business constant)
     * 4. Therefore: fee >= 3.00 (guaranteed positive result)
     */
    @Override
    public Money calculateFee(Money transferAmount, LocalDate scheduleDate, LocalDate transferDate) {
        // Validate business rules before calculation
        if (!isApplicable(scheduleDate, transferDate)) {
            throw new IllegalArgumentException("Fee policy not applicable for given dates");
        }
        
        // Break down calculation into clear steps
        Money percentageFee = calculatePercentageFee(transferAmount);
        return percentageFee.add(BASE_FEE);
    }

    @Override
    public boolean isApplicable(LocalDate scheduleDate, LocalDate transferDate) {
        return daysBetween(scheduleDate, transferDate) == SAME_DAY;
    }

    private Money calculatePercentageFee(Money amount) {
        return Money.of(amount.getAmount().multiply(PERCENTAGE_RATE));
    }

    private long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
}

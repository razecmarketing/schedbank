package com.bank.scheduler.domain.policies.factory;

import com.bank.scheduler.domain.policies.FeePolicy;
import com.bank.scheduler.domain.policies.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Fee Policy Factory - Strategy Selection and Management
 * 
 * Responsibilities:
 * - Select appropriate fee policy based on date ranges
 * - Manage policy registry and lifecycle
 * - Provide centralized policy creation
 * 
 * Design benefits:
 * - Easy to add new policies without changing existing code
 * - Centralized policy selection logic
 * - Stateless design for better scalability
 */
public final class FeePolicyFactory {
    
    // Policy registry for easy management and extension
    private static final List<FeePolicy> POLICIES = List.of(
        new SameDayFeePolicy(),
        new TenDaysFeePolicy(),                    // 1-10 days
        new ElevenToTwentyDaysFeePolicy(),        // 11-20 days  
        new TwentyOneToThirtyDaysFeePolicy(),     // 21-30 days
        new ThirtyOneToFortyDaysFeePolicy(),      // 31-40 days
        new FortyOneToFiftyDaysFeePolicy()        // 41-50 days
    );
    
    /**
     * Factory method to select appropriate fee policy.
     * 
     * Business logic: Find first policy that applies to the given date range
     * 
     * @param scheduleDate When transfer was scheduled
     * @param transferDate When transfer will execute  
     * @return Applicable fee policy
     * @throws IllegalArgumentException if no policy applies (business rule violation)
     */
    public static FeePolicy getPolicyFor(LocalDate scheduleDate, LocalDate transferDate) {
        // Search for applicable policy in order of precedence
        Optional<FeePolicy> applicablePolicy = POLICIES.stream()
            .filter(policy -> policy.isApplicable(scheduleDate, transferDate))
            .findFirst();
            
        return applicablePolicy.orElseThrow(() -> 
            new IllegalArgumentException(
                String.format("No fee policy applicable for transfer from %s to %s", 
                    scheduleDate, transferDate)
            )
        );
    }
    
    /**
     * Utility method for calculating days between dates.
     * Centralized to ensure consistent calculation across all policies.
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * Business rule validation: Transfer date cannot be more than 50 days in future.
     */
    public static boolean isWithinAllowedRange(LocalDate scheduleDate, LocalDate transferDate) {
        long days = daysBetween(scheduleDate, transferDate);
        return days >= 0 && days <= 50;
    }
    
    // Prevent instantiation - this is a utility class
    private FeePolicyFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
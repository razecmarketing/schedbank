package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Fee Policy Integration Tests")
class FeePolicyIntegrationTest {
    
    @Autowired
    private List<FeePolicy> feePolicies;

    @Test
    @DisplayName("Should have exactly one applicable policy for each valid transfer date")
    void shouldHaveExactlyOneApplicablePolicyForEachValidDate() {
        LocalDate scheduleDate = LocalDate.now();
        
        // Test all days from 0 to 50
        for (int days = 0; days <= 50; days++) {
            LocalDate transferDate = scheduleDate.plusDays(days);
            long applicablePolicies = feePolicies.stream()
                .filter(policy -> policy.isApplicable(scheduleDate, transferDate))
                .count();
            
            assertEquals(1, applicablePolicies, 
                String.format("Expected exactly one policy for %d days difference", days));
        }
    }

    @Test
    @DisplayName("Should not have any applicable policy for transfers beyond 50 days")
    void shouldNotHaveApplicablePolicyBeyond50Days() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate = scheduleDate.plusDays(51);
        
        boolean hasApplicablePolicy = feePolicies.stream()
            .anyMatch(policy -> policy.isApplicable(scheduleDate, transferDate));
        
        assertFalse(hasApplicablePolicy);
    }

    @Test
    @DisplayName("Should calculate correct fee for each policy range")
    void shouldCalculateCorrectFeeForEachRange() {
        
        // Same day (2.5% + R$3.00)
        testFeeCalculation(0, new BigDecimal("5.50"));
        
        // 1-10 days (R$12.00)
        testFeeCalculation(5, new BigDecimal("12.00"));
        
        // 11-20 days (8.2%)
        testFeeCalculation(15, new BigDecimal("8.20"));
        
        // 21-30 days (6.9%)
        testFeeCalculation(25, new BigDecimal("6.90"));
        
        // 31-40 days (4.7%)
        testFeeCalculation(35, new BigDecimal("4.70"));
        
        // 41-50 days (1.7%)
        testFeeCalculation(45, new BigDecimal("1.70"));
    }

    private void testFeeCalculation(int days, BigDecimal expectedFee) {
        var amount = Money.of(new BigDecimal("100.00"));
        var scheduleDate = LocalDate.now();
        var transferDate = scheduleDate.plusDays(days);
        
        var policy = feePolicies.stream()
            .filter(p -> p.isApplicable(scheduleDate, transferDate))
            .findFirst()
            .orElseThrow();
        
        var actualFee = policy.calculateFee(amount, scheduleDate, transferDate);
        assertEquals(0, expectedFee.compareTo(actualFee.getAmount()));
    }
}

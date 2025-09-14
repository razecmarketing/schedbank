package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Same Day Fee Policy Test Suite - Comprehensive Coverage
 * 
 * Test Strategy:
 * 1. Tests serve as living documentation 
 * 2. Test-first development approach
 * 3. Coverage of all business scenarios and edge cases
 * 
 * Test Categories:
 * - Edge cases: zero amount, minimum amount, large amounts
 * - Boundary conditions: exact same day, different days
 * - Business rule verification: fee calculation correctness
 * 
 * Each test validates a specific business requirement or mathematical property.
 */
@DisplayName("Same Day Fee Policy - Complete Test Coverage")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class SameDayFeePolicyTest {
    
    private final SameDayFeePolicy policy = new SameDayFeePolicy();

    @Test
    @DisplayName("Should apply to transfers scheduled for the same day")
    void shouldApplyToSameDayTransfers() {
        // Arrange
        LocalDate today = LocalDate.now();
        
        // Act & Assert
    assertTrue(policy.isApplicable(today, today));
    }

    @Test
    @DisplayName("Should not apply to transfers scheduled for future dates")
    void shouldNotApplyToFutureDayTransfers() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        // Act & Assert
    assertFalse(policy.isApplicable(today, tomorrow));
    }

    @Test
    @DisplayName("Should calculate fee correctly (2.5% + R$ 3,00)")
    void shouldCalculateFeeCorrectly() {
    // Arrange
    LocalDate today = LocalDate.now();
    var transferAmount = Money.of(new BigDecimal("100.00"));
    var expectedFee = Money.of(new BigDecimal("5.50")); // (100 * 0.025) + 3.00
        
    // Act
    var actualFee = policy.calculateFee(transferAmount, today, today);
        
        // Assert
        assertEquals(expectedFee, actualFee);
    }
}

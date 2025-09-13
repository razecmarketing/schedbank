package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Same Day Fee Policy Tests")
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

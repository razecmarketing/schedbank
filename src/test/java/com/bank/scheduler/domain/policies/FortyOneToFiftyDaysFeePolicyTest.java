package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Forty One To Fifty Days Fee Policy Tests")
class FortyOneToFiftyDaysFeePolicyTest {
    private final FortyOneToFiftyDaysFeePolicy policy = new FortyOneToFiftyDaysFeePolicy();

    @Test
    @DisplayName("Should apply to transfers between 41 and 50 days")
    void shouldApplyToBetweenFortyOneAndFiftyDays() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate41Days = scheduleDate.plusDays(41);
        LocalDate transferDate50Days = scheduleDate.plusDays(50);
        
    assertTrue(policy.isApplicable(scheduleDate, transferDate41Days));
    assertTrue(policy.isApplicable(scheduleDate, transferDate50Days));
    }

    @Test
    @DisplayName("Should not apply to transfers outside 41-50 days range")
    void shouldNotApplyToTransfersOutsideRange() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate40Days = scheduleDate.plusDays(40);
        LocalDate transferDate51Days = scheduleDate.plusDays(51);
        
    assertFalse(policy.isApplicable(scheduleDate, transferDate40Days));
    assertFalse(policy.isApplicable(scheduleDate, transferDate51Days));
    }

    @Test
    @DisplayName("Should calculate fee correctly (1.7%)")
    void shouldCalculateFeeCorrectly() {
    var scheduleDate = LocalDate.now();
    var transferDate41Days = scheduleDate.plusDays(41);
    var amount = Money.of(new BigDecimal("100.00"));
    var expectedFee = Money.of(new BigDecimal("1.70"));
        
    var actualFee = policy.calculateFee(amount, scheduleDate, transferDate41Days);
        
        assertEquals(expectedFee, actualFee);
    }
}

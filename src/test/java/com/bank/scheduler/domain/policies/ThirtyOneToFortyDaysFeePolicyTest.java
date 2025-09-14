package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Thirty One To Forty Days Fee Policy Tests")
class ThirtyOneToFortyDaysFeePolicyTest {
    private final ThirtyOneToFortyDaysFeePolicy policy = new ThirtyOneToFortyDaysFeePolicy();

    @Test
    @DisplayName("Should apply to transfers between 31 and 40 days")
    void shouldApplyToBetweenThirtyOneAndFortyDays() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate31Days = scheduleDate.plusDays(31);
        LocalDate transferDate40Days = scheduleDate.plusDays(40);
        
    assertTrue(policy.isApplicable(scheduleDate, transferDate31Days));
    assertTrue(policy.isApplicable(scheduleDate, transferDate40Days));
    }

    @Test
    @DisplayName("Should not apply to transfers outside 31-40 days range")
    void shouldNotApplyToTransfersOutsideRange() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate30Days = scheduleDate.plusDays(30);
        LocalDate transferDate41Days = scheduleDate.plusDays(41);
        
    assertFalse(policy.isApplicable(scheduleDate, transferDate30Days));
    assertFalse(policy.isApplicable(scheduleDate, transferDate41Days));
    }

    @Test
    @DisplayName("Should calculate fee correctly (4.7%)")
    void shouldCalculateFeeCorrectly() {
    var scheduleDate = LocalDate.now();
    var transferDate31Days = scheduleDate.plusDays(31);
    var amount = Money.of(new BigDecimal("100.00"));
    var expectedFee = Money.of(new BigDecimal("4.70"));
        
    var actualFee = policy.calculateFee(amount, scheduleDate, transferDate31Days);
        
        assertEquals(expectedFee, actualFee);
    }
}

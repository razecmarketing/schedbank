package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Twenty One To Thirty Days Fee Policy Tests")
class TwentyOneToThirtyDaysFeePolicyTest {
    private final TwentyOneToThirtyDaysFeePolicy policy = new TwentyOneToThirtyDaysFeePolicy();

    @Test
    @DisplayName("Should apply to transfers between 21 and 30 days")
    void shouldApplyToBetweenTwentyOneAndThirtyDays() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate21Days = scheduleDate.plusDays(21);
        LocalDate transferDate30Days = scheduleDate.plusDays(30);
        
    assertTrue(policy.isApplicable(scheduleDate, transferDate21Days));
    assertTrue(policy.isApplicable(scheduleDate, transferDate30Days));
    }

    @Test
    @DisplayName("Should not apply to transfers outside 21-30 days range")
    void shouldNotApplyToTransfersOutsideRange() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate20Days = scheduleDate.plusDays(20);
        LocalDate transferDate31Days = scheduleDate.plusDays(31);
        
    assertFalse(policy.isApplicable(scheduleDate, transferDate20Days));
    assertFalse(policy.isApplicable(scheduleDate, transferDate31Days));
    }

    @Test
    @DisplayName("Should calculate fee correctly (6.9%)")
    void shouldCalculateFeeCorrectly() {
    var scheduleDate = LocalDate.now();
    var transferDate21Days = scheduleDate.plusDays(21);
    var amount = Money.of(new BigDecimal("100.00"));
    var expectedFee = Money.of(new BigDecimal("6.90"));
        
    var actualFee = policy.calculateFee(amount, scheduleDate, transferDate21Days);
        
        assertEquals(expectedFee, actualFee);
    }
}

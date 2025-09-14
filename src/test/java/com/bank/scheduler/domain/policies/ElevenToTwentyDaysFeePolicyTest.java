package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Eleven To Twenty Days Fee Policy Tests")
class ElevenToTwentyDaysFeePolicyTest {
    private final ElevenToTwentyDaysFeePolicy policy = new ElevenToTwentyDaysFeePolicy();

    @Test
    @DisplayName("Should apply to transfers between 11 and 20 days")
    void shouldApplyToBetweenElevenAndTwentyDays() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate11Days = scheduleDate.plusDays(11);
        LocalDate transferDate20Days = scheduleDate.plusDays(20);
        
    assertTrue(policy.isApplicable(scheduleDate, transferDate11Days));
    assertTrue(policy.isApplicable(scheduleDate, transferDate20Days));
    }

    @Test
    @DisplayName("Should not apply to transfers outside 11-20 days range")
    void shouldNotApplyToTransfersOutsideRange() {
        LocalDate scheduleDate = LocalDate.now();
        LocalDate transferDate10Days = scheduleDate.plusDays(10);
        LocalDate transferDate21Days = scheduleDate.plusDays(21);
        
    assertFalse(policy.isApplicable(scheduleDate, transferDate10Days));
    assertFalse(policy.isApplicable(scheduleDate, transferDate21Days));
    }

    @Test
    @DisplayName("Should calculate fee correctly (8.2%)")
    void shouldCalculateFeeCorrectly() {
    var scheduleDate = LocalDate.now();
    var transferDate11Days = scheduleDate.plusDays(11);
    var amount = Money.of(new BigDecimal("100.00"));
    var expectedFee = Money.of(new BigDecimal("8.20"));
        
    var actualFee = policy.calculateFee(amount, scheduleDate, transferDate11Days);
        
        assertEquals(expectedFee, actualFee);
    }
}

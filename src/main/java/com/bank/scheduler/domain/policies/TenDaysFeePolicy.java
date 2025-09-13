package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class TenDaysFeePolicy implements FeePolicy {
    private static final BigDecimal FIXED_FEE = new BigDecimal("12.00");

    @Override
    public Money calculateFee(Money transferAmount, LocalDate scheduleDate, LocalDate transferDate) {
        return Money.of(FIXED_FEE);
    }

    @Override
    public boolean isApplicable(LocalDate scheduleDate, LocalDate transferDate) {
        long days = ChronoUnit.DAYS.between(scheduleDate, transferDate);
        return days >= 1 && days <= 10;
    }
}

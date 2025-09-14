package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class TwentyOneToThirtyDaysFeePolicy implements FeePolicy {
    private static final BigDecimal PERCENTAGE_RATE = new BigDecimal("0.069");
    private static final int MIN_DAYS = 21;
    private static final int MAX_DAYS = 30;

    @Override
    public Money calculateFee(Money transferAmount, LocalDate scheduleDate, LocalDate transferDate) {
        if (!isApplicable(scheduleDate, transferDate)) {
            throw new IllegalArgumentException("Fee policy not applicable for given dates");
        }
        return Money.of(transferAmount.getAmount().multiply(PERCENTAGE_RATE));
    }

    @Override
    public boolean isApplicable(LocalDate scheduleDate, LocalDate transferDate) {
        long days = ChronoUnit.DAYS.between(scheduleDate, transferDate);
        return days >= MIN_DAYS && days <= MAX_DAYS;
    }
}

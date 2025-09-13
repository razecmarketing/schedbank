package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class SameDayFeePolicy implements FeePolicy {
    private static final BigDecimal PERCENTAGE_RATE = new BigDecimal("0.025");
    private static final Money BASE_FEE = Money.of(new BigDecimal("3.00"));
    private static final int SAME_DAY = 0;

    @Override
    public Money calculateFee(Money transferAmount, LocalDate scheduleDate, LocalDate transferDate) {
        if (!isApplicable(scheduleDate, transferDate)) {
            throw new IllegalArgumentException("Fee policy not applicable for given dates");
        }
        return calculatePercentageFee(transferAmount).add(BASE_FEE);
    }

    @Override
    public boolean isApplicable(LocalDate scheduleDate, LocalDate transferDate) {
        return daysBetween(scheduleDate, transferDate) == SAME_DAY;
    }

    private Money calculatePercentageFee(Money amount) {
        return Money.of(amount.getAmount().multiply(PERCENTAGE_RATE));
    }

    private long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
}

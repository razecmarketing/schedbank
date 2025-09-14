package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import java.time.LocalDate;

public interface FeePolicy {
    Money calculateFee(Money transferAmount, LocalDate scheduleDate, LocalDate transferDate);
    boolean isApplicable(LocalDate scheduleDate, LocalDate transferDate);
}

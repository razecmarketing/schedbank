package com.bank.scheduler.domain.specifications;

import java.time.LocalDate;

public interface TransferSpecification {
    boolean isSatisfiedBy(LocalDate scheduleDate, LocalDate transferDate);
}

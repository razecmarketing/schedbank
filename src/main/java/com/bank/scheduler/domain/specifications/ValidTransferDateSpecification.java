package com.bank.scheduler.domain.specifications;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class ValidTransferDateSpecification implements TransferSpecification {
    private static final int MAX_SCHEDULE_DAYS = 50;
    
    @Override
    public boolean isSatisfiedBy(LocalDate scheduleDate, LocalDate transferDate) {
        if (transferDate == null || scheduleDate == null) {
            return false;
        }
        
        if (transferDate.isBefore(scheduleDate)) {
            return false;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(scheduleDate, transferDate);
        return daysBetween <= MAX_SCHEDULE_DAYS;
    }
}

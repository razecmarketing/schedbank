package com.bank.scheduler.domain.policies;

import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FeePolicyTest {
    private SameDayFeePolicy sameDayPolicy;
    private TenDaysFeePolicy tenDaysPolicy;
    private ElevenToTwentyDaysFeePolicy elevenToTwentyPolicy;
    private TwentyOneToThirtyDaysFeePolicy twentyOneToThirtyPolicy;
    private ThirtyOneToFortyDaysFeePolicy thirtyOneToFortyPolicy;
    private FortyOneToFiftyDaysFeePolicy fortyOneToFiftyPolicy;
    private LocalDate scheduleDate;
    private Money transferAmount;

    @BeforeEach
    void setUp() {
        sameDayPolicy = new SameDayFeePolicy();
        tenDaysPolicy = new TenDaysFeePolicy();
        elevenToTwentyPolicy = new ElevenToTwentyDaysFeePolicy();
        twentyOneToThirtyPolicy = new TwentyOneToThirtyDaysFeePolicy();
        thirtyOneToFortyPolicy = new ThirtyOneToFortyDaysFeePolicy();
        fortyOneToFiftyPolicy = new FortyOneToFiftyDaysFeePolicy();
        scheduleDate = LocalDate.now();
        transferAmount = Money.of(new BigDecimal("1000.00"));
    }

    @Test
    void sameDayFeeCalculation() {
        LocalDate transferDate = scheduleDate;
        Money fee = sameDayPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        
        BigDecimal expected = new BigDecimal("28.00"); // 2.5% de 1000 + 3.00
        assertEquals(0, fee.getAmount().compareTo(expected), "Same day fee should be 2.5% plus R$3.00");
    }

    @Test
    void tenDaysFeeCalculation() {
        LocalDate transferDate = scheduleDate.plusDays(5);
        Money fee = tenDaysPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        
        BigDecimal expected = new BigDecimal("12.00");
        assertEquals(0, fee.getAmount().compareTo(expected), "1-10 days fee should be R$12.00");
    }

    @Test
    void elevenToTwentyDaysFeeCalculation() {
        LocalDate transferDate = scheduleDate.plusDays(15);
        Money fee = elevenToTwentyPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        
        BigDecimal expected = new BigDecimal("82.00"); // 8.2% de 1000
        assertEquals(0, fee.getAmount().compareTo(expected), "11-20 days fee should be 8.2%");
    }

    @Test
    void twentyOneToThirtyDaysFeeCalculation() {
        LocalDate transferDate = scheduleDate.plusDays(25);
        Money fee = twentyOneToThirtyPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        
        BigDecimal expected = new BigDecimal("69.00"); // 6.9% de 1000
        assertEquals(0, fee.getAmount().compareTo(expected), "21-30 days fee should be 6.9%");
    }

    @Test
    void thirtyOneToFortyDaysFeeCalculation() {
        LocalDate transferDate = scheduleDate.plusDays(35);
        Money fee = thirtyOneToFortyPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        
        BigDecimal expected = new BigDecimal("47.00"); // 4.7% de 1000
        assertEquals(0, fee.getAmount().compareTo(expected), "31-40 days fee should be 4.7%");
    }

    @Test
    void fortyOneToFiftyDaysFeeCalculation() {
        LocalDate transferDate = scheduleDate.plusDays(45);
        Money fee = fortyOneToFiftyPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        
        BigDecimal expected = new BigDecimal("17.00"); // 1.7% de 1000
        assertEquals(0, fee.getAmount().compareTo(expected), "41-50 days fee should be 1.7%");
    }

    @Test
    void shouldThrowExceptionForInvalidPeriod() {
        LocalDate transferDate = scheduleDate.plusDays(51);
        assertThrows(IllegalArgumentException.class, () -> {
            fortyOneToFiftyPolicy.calculateFee(transferAmount, scheduleDate, transferDate);
        }, "Should throw exception for period > 50 days");
    }
}

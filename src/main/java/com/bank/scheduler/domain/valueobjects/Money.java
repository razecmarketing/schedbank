package com.bank.scheduler.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {
    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        validatePreconditions(amount);
        this.amount = amount.setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private void validatePreconditions(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        // Remover validação de casas decimais pois será arredondado automaticamente
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null Money");
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null Money");
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money subtraction would result in negative amount");
        }
        return new Money(result);
    }

    public Money multiply(BigDecimal multiplier) {
        Objects.requireNonNull(multiplier, "Multiplier cannot be null");
        return new Money(this.amount.multiply(multiplier));
    }

    public Money percentage(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "Percentage cannot be null");
        BigDecimal multiplier = percentage.divide(new BigDecimal("100"), DECIMAL_PLACES + 2, ROUNDING_MODE);
        return multiply(multiplier);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}

package com.bank.scheduler.domain.properties;

import com.bank.scheduler.domain.valueobjects.AccountNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("Account Number Specification")
class AccountNumberPropertiesTest {

    @Nested
    @DisplayName("Account number validation should")
    class ValidationTests {
        
        @Test
        @DisplayName("reject null values")
        void rejectNull() {
            assertThrows(IllegalArgumentException.class, 
                () -> AccountNumber.of(null));
        }

        @ParameterizedTest(name = "reject invalid length: {0}")
        @ValueSource(strings = {"123", "12345", "123456789", "12345678901"})
        void rejectInvalidLength(String invalidAccount) {
            assertThrows(IllegalArgumentException.class, 
                () -> AccountNumber.of(invalidAccount));
        }

        @ParameterizedTest(name = "reject non-numeric: {0}")
        @ValueSource(strings = {"abcd123456", "123456789a", "1234-56789"})
        void rejectNonNumeric(String invalidAccount) {
            assertThrows(IllegalArgumentException.class, 
                () -> AccountNumber.of(invalidAccount));
        }

        @ParameterizedTest(name = "accept valid account: {0}")
        @ValueSource(strings = {"1234567897", "9876543210"})
        void acceptValidAccount(String validAccount) {
            AccountNumber account = AccountNumber.of(validAccount);
            assertEquals(validAccount, account.getValue());
        }
    }

    @Nested
    @DisplayName("Account number equality should")
    class EqualityTests {
        
        @Test
        @DisplayName("consider same number equal")
        void considerSameNumberEqual() {
            AccountNumber acc1 = AccountNumber.of("1234567897");
            AccountNumber acc2 = AccountNumber.of("1234567897");
            assertEquals(acc1, acc2);
            assertEquals(acc1.hashCode(), acc2.hashCode());
        }

        @Test
        @DisplayName("consider different numbers not equal")
        void considerDifferentNumbersNotEqual() {
            AccountNumber acc1 = AccountNumber.of("1234567897");
            AccountNumber acc2 = AccountNumber.of("9876543210");
            assertNotEquals(acc1, acc2);
        }
    }
}

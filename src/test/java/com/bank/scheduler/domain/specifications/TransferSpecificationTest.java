package com.bank.scheduler.domain.specifications;

import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based testing for TransferSpecification following TDD principles.
 * 
 * Testing Philosophy:
 * - Red → Green → Refactor cycle
 * - Properties hold for all valid inputs
 * - Edge cases explicitly tested
 * - Mathematical proofs validated through code
 */
@DisplayName("Transfer Specification - Mathematical Properties")
class TransferSpecificationTest {
    
    @Nested
    @DisplayName("Temporal Order Invariant")
    class TemporalOrderTests {
        
        @Test
        @DisplayName("should accept transfer date same as schedule date")
        void sameDate() {
            LocalDate date = LocalDate.now();
            assertTrue(TransferSpecification.hasValidTemporalOrder(date, date));
        }
        
        @Test
        @DisplayName("should accept transfer date after schedule date")
        void futureTransferDate() {
            LocalDate scheduleDate = LocalDate.now();
            LocalDate transferDate = scheduleDate.plusDays(1);
            assertTrue(TransferSpecification.hasValidTemporalOrder(scheduleDate, transferDate));
        }
        
        @Test
        @DisplayName("should reject transfer date before schedule date")
        void pastTransferDate() {
            LocalDate scheduleDate = LocalDate.now();
            LocalDate transferDate = scheduleDate.minusDays(1);
            assertFalse(TransferSpecification.hasValidTemporalOrder(scheduleDate, transferDate));
        }
        
        @Test
        @DisplayName("should reject null dates")
        void nullDates() {
            assertFalse(TransferSpecification.hasValidTemporalOrder(null, LocalDate.now()));
            assertFalse(TransferSpecification.hasValidTemporalOrder(LocalDate.now(), null));
            assertFalse(TransferSpecification.hasValidTemporalOrder(null, null));
        }
        
        @ParameterizedTest(name = "transfer {0} days in future should be valid")
        @ValueSource(ints = {0, 1, 7, 30, 50, 365})
        @DisplayName("should accept all reasonable future dates")
        void propertyFutureDatesValid(int daysInFuture) {
            LocalDate scheduleDate = LocalDate.now();
            LocalDate transferDate = scheduleDate.plusDays(daysInFuture);
            
            assertTrue(TransferSpecification.hasValidTemporalOrder(scheduleDate, transferDate),
                "Transfer " + daysInFuture + " days in future should be temporally valid");
        }
    }
    
    @Nested
    @DisplayName("Account Distinctness Invariant")
    class AccountDistinctnessTests {
        
        @Test
        @DisplayName("should accept different accounts")
        void differentAccounts() {
            assertTrue(TransferSpecification.hasDistinctAccounts("1234567890", "0987654321"));
        }
        
        @Test
        @DisplayName("should reject identical accounts")
        void sameAccount() {
            assertFalse(TransferSpecification.hasDistinctAccounts("1234567890", "1234567890"));
        }
        
        @Test
        @DisplayName("should reject null accounts")
        void nullAccounts() {
            assertFalse(TransferSpecification.hasDistinctAccounts(null, "1234567890"));
            assertFalse(TransferSpecification.hasDistinctAccounts("1234567890", null));
            assertFalse(TransferSpecification.hasDistinctAccounts(null, null));
        }
        
        @ParameterizedTest(name = "accounts {0} and {1} should be distinct")
        @ValueSource(strings = {"1111111111", "2222222222", "9999999999"})
        @DisplayName("should validate distinctness property")
        void propertyAccountDistinctness(String targetAccount) {
            String sourceAccount = "1234567890";
            boolean expected = !sourceAccount.equals(targetAccount);
            
            assertEquals(expected, 
                TransferSpecification.hasDistinctAccounts(sourceAccount, targetAccount),
                "Account distinctness property violated");
        }
    }
    
    @Nested
    @DisplayName("Monetary Value Invariant")
    class MonetaryValueTests {
        
        @Test
        @DisplayName("should accept positive amount and non-negative fee")
        void validMonetaryValues() {
            BigDecimal amount = new BigDecimal("1000.00");
            BigDecimal fee = new BigDecimal("12.50");
            
            assertTrue(TransferSpecification.hasValidMonetaryValues(amount, fee));
        }
        
        @Test
        @DisplayName("should accept zero fee")
        void zeroFee() {
            BigDecimal amount = new BigDecimal("1000.00");
            BigDecimal fee = BigDecimal.ZERO;
            
            assertTrue(TransferSpecification.hasValidMonetaryValues(amount, fee));
        }
        
        @Test
        @DisplayName("should reject zero or negative amounts")
        void invalidAmounts() {
            BigDecimal validFee = new BigDecimal("12.50");
            
            assertFalse(TransferSpecification.hasValidMonetaryValues(BigDecimal.ZERO, validFee));
            assertFalse(TransferSpecification.hasValidMonetaryValues(new BigDecimal("-100.00"), validFee));
        }
        
        @Test
        @DisplayName("should reject negative fees")
        void negativeFees() {
            BigDecimal validAmount = new BigDecimal("1000.00");
            BigDecimal negativeFee = new BigDecimal("-5.00");
            
            assertFalse(TransferSpecification.hasValidMonetaryValues(validAmount, negativeFee));
        }
        
        @Test
        @DisplayName("should reject null monetary values")
        void nullMonetaryValues() {
            BigDecimal validValue = new BigDecimal("100.00");
            
            assertFalse(TransferSpecification.hasValidMonetaryValues(null, validValue));
            assertFalse(TransferSpecification.hasValidMonetaryValues(validValue, null));
            assertFalse(TransferSpecification.hasValidMonetaryValues(null, null));
        }
        
        @ParameterizedTest(name = "amount {0} should be valid")
        @ValueSource(strings = {"0.01", "1.00", "100.00", "1000.00", "999999.99"})
        @DisplayName("should validate positive amount property")
        void propertyPositiveAmounts(String amountStr) {
            BigDecimal amount = new BigDecimal(amountStr);
            BigDecimal fee = new BigDecimal("10.00");
            
            assertTrue(TransferSpecification.hasValidMonetaryValues(amount, fee),
                "Amount " + amountStr + " should be valid");
        }
    }
    
    @Nested
    @DisplayName("Mathematical Properties - Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("should handle boundary monetary values")
        void boundaryMonetaryValues() {
            // Smallest positive amount
            BigDecimal smallestAmount = new BigDecimal("0.01");
            BigDecimal zeroFee = BigDecimal.ZERO;
            
            assertTrue(TransferSpecification.hasValidMonetaryValues(smallestAmount, zeroFee));
            
            // Large amounts
            BigDecimal largeAmount = new BigDecimal("999999999.99");
            BigDecimal largeFee = new BigDecimal("999999.99");
            
            assertTrue(TransferSpecification.hasValidMonetaryValues(largeAmount, largeFee));
        }
        
        @Test
        @DisplayName("should handle date boundaries")
        void dateBoundaries() {
            LocalDate minDate = LocalDate.MIN;
            LocalDate maxDate = LocalDate.MAX;
            
            // Min to Max should be valid
            assertTrue(TransferSpecification.hasValidTemporalOrder(minDate, maxDate));
            
            // Max to Min should be invalid
            assertFalse(TransferSpecification.hasValidTemporalOrder(maxDate, minDate));
        }
        
        @Test
        @DisplayName("should handle precision in monetary calculations")
        void monetaryPrecision() {
            // Test precision edge cases
            BigDecimal preciseAmount = new BigDecimal("1000.001");
            BigDecimal preciseFee = new BigDecimal("0.001");
            
            assertTrue(TransferSpecification.hasValidMonetaryValues(preciseAmount, preciseFee));
        }
    }
}
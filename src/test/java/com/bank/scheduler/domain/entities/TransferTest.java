package com.bank.scheduler.domain.entities;

import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transfer Entity Tests")
class TransferTest {

    @Nested
    @DisplayName("Transfer Creation")
    class TransferCreation {
        
        @Test
        @DisplayName("Should create valid transfer successfully")
        void shouldCreateValidTransfer() {
            // Arrange
            var sourceAccount = AccountNumber.of("1234567890");
            var targetAccount = AccountNumber.of("0987654321");
            var amount = Money.of(new BigDecimal("100.00"));
            var fee = Money.of(new BigDecimal("2.50"));
            var scheduleDate = LocalDate.now();
            var transferDate = LocalDate.now().plusDays(1);

            // Act
            var transfer = Transfer.schedule(
                sourceAccount,
                targetAccount,
                amount,
                fee,
                scheduleDate,
                transferDate
            );

            // Assert
            assertNotNull(transfer);
            assertEquals(sourceAccount, transfer.getSourceAccount());
            assertEquals(targetAccount, transfer.getTargetAccount());
            assertEquals(amount, transfer.getAmount());
            assertEquals(fee, transfer.getFee());
            assertEquals(scheduleDate, transfer.getScheduleDate());
            assertEquals(transferDate, transfer.getTransferDate());
        }

        @Test
        @DisplayName("Should not allow transfer with past date")
        void shouldNotAllowTransferWithPastDate() {
            // Arrange
            var sourceAccount = AccountNumber.of("1234567890");
            var targetAccount = AccountNumber.of("0987654321");
            var amount = Money.of(new BigDecimal("100.00"));
            var fee = Money.of(new BigDecimal("2.50"));
            var scheduleDate = LocalDate.now();
            var transferDate = LocalDate.now().minusDays(1);

            // Act & Assert
            assertThrows(DomainException.InvalidTransferDate.class, () ->
                Transfer.schedule(
                    sourceAccount,
                    targetAccount,
                    amount,
                    fee,
                    scheduleDate,
                    transferDate
                )
            );
        }

        @Test
        @DisplayName("Should not allow transfer to same account")
        void shouldNotAllowTransferToSameAccount() {
            // Arrange
            var account = AccountNumber.of("1234567890");
            var amount = Money.of(new BigDecimal("100.00"));
            var fee = Money.of(new BigDecimal("2.50"));
            var scheduleDate = LocalDate.now();
            var transferDate = LocalDate.now().plusDays(1);

            // Act & Assert
            assertThrows(DomainException.class, () ->
                Transfer.schedule(
                    account,
                    account,
                    amount,
                    fee,
                    scheduleDate,
                    transferDate
                )
            );
        }
    }
}

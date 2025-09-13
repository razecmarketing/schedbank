package com.bank.scheduler.application.usecases;

import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.policies.FeePolicy;
import com.bank.scheduler.domain.ports.TransferRepository;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferSchedulerServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private FeePolicy feePolicy;

    private TransferSchedulerService service;
    private Set<FeePolicy> feePolicies;

    @BeforeEach
    void setUp() {
        feePolicies = new HashSet<>();
        feePolicies.add(feePolicy);
        service = new TransferSchedulerService(transferRepository, feePolicies);
    }

    @Test
    void shouldScheduleValidTransfer() {
        // Arrange
        String sourceAccount = "1234567890";
        String targetAccount = "0987654321";
        BigDecimal amount = new BigDecimal("1000.00");
        LocalDate transferDate = LocalDate.now();
        Money expectedFee = Money.of(new BigDecimal("28.00"));

        when(feePolicy.isApplicable(any(), any())).thenReturn(true);
        when(feePolicy.calculateFee(any(), any(), any())).thenReturn(expectedFee);
        when(transferRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Transfer transfer = service.scheduleTransfer(sourceAccount, targetAccount, Money.of(amount), transferDate);

        // Assert
        assertNotNull(transfer);
        assertEquals(sourceAccount, transfer.getSourceAccount().getValue());
        assertEquals(targetAccount, transfer.getTargetAccount().getValue());
        assertEquals(0, transfer.getAmount().getAmount().compareTo(amount));
        assertEquals(0, transfer.getFee().getAmount().compareTo(expectedFee.getAmount()));
        
        verify(transferRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNoApplicableFeePolicy() {
        // Arrange
        String sourceAccount = "1234567890";
        String targetAccount = "0987654321";
        BigDecimal amount = new BigDecimal("1000.00");
        LocalDate transferDate = LocalDate.now().plusDays(51);

        when(feePolicy.isApplicable(any(), any())).thenReturn(false);

        // Act & Assert
        assertThrows(DomainException.NoApplicableFeePolicy.class, () -> 
            service.scheduleTransfer(sourceAccount, targetAccount, Money.of(amount), transferDate),
            "Should throw exception when no fee policy is applicable"
        );

        verify(transferRepository, never()).save(any());
    }
}

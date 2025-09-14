package com.bank.scheduler.application.usecases;

import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.ports.TransferRepository;
import com.bank.scheduler.domain.policies.FeePolicy;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("TransferSchedulerService Delete Operations Tests")
class TransferSchedulerServiceDeleteTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private Set<FeePolicy> feePolicies;

    private TransferSchedulerService transferSchedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferSchedulerService = new TransferSchedulerService(transferRepository, feePolicies);
    }

    @Test
    @DisplayName("Should delete transfer successfully when transfer exists")
    void shouldDeleteTransferSuccessfullyWhenTransferExists() {
        UUID transferId = UUID.randomUUID();
        when(transferRepository.existsById(transferId)).thenReturn(true);

        assertDoesNotThrow(() -> transferSchedulerService.deleteTransfer(transferId));

        verify(transferRepository).existsById(transferId);
        verify(transferRepository).deleteById(transferId);
    }

    @Test
    @DisplayName("Should throw InvalidTransferData when transfer ID is null")
    void shouldThrowInvalidTransferDataWhenTransferIdIsNull() {
        DomainException.InvalidTransferData exception = assertThrows(
            DomainException.InvalidTransferData.class,
            () -> transferSchedulerService.deleteTransfer(null)
        );

        assertEquals("Transfer ID cannot be null", exception.getMessage());
        verify(transferRepository, never()).existsById(any());
        verify(transferRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw TransferNotFound when transfer does not exist")
    void shouldThrowTransferNotFoundWhenTransferDoesNotExist() {
        UUID transferId = UUID.randomUUID();
        when(transferRepository.existsById(transferId)).thenReturn(false);

        DomainException.TransferNotFound exception = assertThrows(
            DomainException.TransferNotFound.class,
            () -> transferSchedulerService.deleteTransfer(transferId)
        );

        assertEquals("Transfer with ID " + transferId + " not found", exception.getMessage());
        verify(transferRepository).existsById(transferId);
        verify(transferRepository, never()).deleteById(transferId);
    }

    @Test
    @DisplayName("Should clear all transfers successfully")
    void shouldClearAllTransfersSuccessfully() {
        assertDoesNotThrow(() -> transferSchedulerService.clearAllTransfers());
        verify(transferRepository).deleteAll();
    }

    @Test
    @DisplayName("Should validate all parameters when scheduling transfer")
    void shouldValidateAllParametersWhenSchedulingTransfer() {
        DomainException.InvalidTransferData exception = assertThrows(
            DomainException.InvalidTransferData.class,
            () -> transferSchedulerService.scheduleTransfer(null, "1234567890", Money.of(BigDecimal.valueOf(100)), LocalDate.now().plusDays(1))
        );

        assertEquals("Account numbers cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate amount when scheduling transfer")
    void shouldValidateAmountWhenSchedulingTransfer() {
        DomainException.InvalidTransferData exception = assertThrows(
            DomainException.InvalidTransferData.class,
            () -> transferSchedulerService.scheduleTransfer("1234567890", "0987654321", null, LocalDate.now().plusDays(1))
        );

        assertEquals("Transfer amount cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate transfer date when scheduling transfer")
    void shouldValidateTransferDateWhenSchedulingTransfer() {
        DomainException.InvalidTransferData exception = assertThrows(
            DomainException.InvalidTransferData.class,
            () -> transferSchedulerService.scheduleTransfer("1234567890", "0987654321", Money.of(BigDecimal.valueOf(100)), null)
        );

        assertEquals("Transfer date cannot be null", exception.getMessage());
    }
}
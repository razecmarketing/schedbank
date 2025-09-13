package com.bank.scheduler.infrastructure.web.controllers;

import com.bank.scheduler.application.usecases.TransferSchedulerService;
import com.bank.scheduler.domain.exceptions.DomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
@DisplayName("TransferController Delete Operations Tests")
class TransferControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferSchedulerService transferSchedulerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should delete transfer successfully and return 204 No Content")
    void shouldDeleteTransferSuccessfullyAndReturn204() throws Exception {
        UUID transferId = UUID.randomUUID();

        mockMvc.perform(delete("/api/transfers/{id}", transferId))
                .andExpect(status().isNoContent());

        verify(transferSchedulerService).deleteTransfer(transferId);
    }

    @Test
    @DisplayName("Should return 404 when transfer not found for deletion")
    void shouldReturn404WhenTransferNotFoundForDeletion() throws Exception {
        UUID transferId = UUID.randomUUID();
        doThrow(new DomainException.TransferNotFound("Transfer not found"))
                .when(transferSchedulerService).deleteTransfer(transferId);

        mockMvc.perform(delete("/api/transfers/{id}", transferId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transfer not found"));

        verify(transferSchedulerService).deleteTransfer(transferId);
    }

    @Test
    @DisplayName("Should return 400 when transfer ID is invalid")
    void shouldReturn400WhenTransferIdIsInvalid() throws Exception {
        UUID transferId = UUID.randomUUID();
        doThrow(new DomainException.InvalidTransferData("Invalid transfer ID"))
                .when(transferSchedulerService).deleteTransfer(transferId);

        mockMvc.perform(delete("/api/transfers/{id}", transferId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid transfer ID"));

        verify(transferSchedulerService).deleteTransfer(transferId);
    }

    @Test
    @DisplayName("Should clear all transfers successfully and return 204 No Content")
    void shouldClearAllTransfersSuccessfullyAndReturn204() throws Exception {
        mockMvc.perform(delete("/api/transfers"))
                .andExpect(status().isNoContent());

        verify(transferSchedulerService).clearAllTransfers();
    }

    @Test
    @DisplayName("Should handle service errors when clearing all transfers")
    void shouldHandleServiceErrorsWhenClearingAllTransfers() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(transferSchedulerService).clearAllTransfers();

        mockMvc.perform(delete("/api/transfers"))
                .andExpect(status().isInternalServerError());

        verify(transferSchedulerService).clearAllTransfers();
    }
}
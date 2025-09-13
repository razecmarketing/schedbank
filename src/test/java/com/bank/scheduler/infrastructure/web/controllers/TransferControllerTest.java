package com.bank.scheduler.infrastructure.web.controllers;

import com.bank.scheduler.application.usecases.TransferSchedulerService;
import com.bank.scheduler.domain.valueobjects.Money;
import com.bank.scheduler.infrastructure.web.dtos.ScheduleTransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferSchedulerService transferSchedulerService;

    @Test
    void shouldScheduleValidTransfer() throws Exception {
        ScheduleTransferRequest request = new ScheduleTransferRequest();
        request.setSourceAccount("1234567890");
        request.setTargetAccount("0987654321");
        request.setAmount(new BigDecimal("1000.00"));
        request.setTransferDate(LocalDate.now());

        mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidAccountNumber() throws Exception {
        ScheduleTransferRequest request = new ScheduleTransferRequest();
        request.setSourceAccount("123"); // Invalid account number
        request.setTargetAccount("0987654321");
        request.setAmount(new BigDecimal("1000.00"));
        request.setTransferDate(LocalDate.now());

        mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNegativeAmount() throws Exception {
        ScheduleTransferRequest request = new ScheduleTransferRequest();
        request.setSourceAccount("1234567890");
        request.setTargetAccount("0987654321");
        request.setAmount(new BigDecimal("-1000.00"));
        request.setTransferDate(LocalDate.now());

        mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

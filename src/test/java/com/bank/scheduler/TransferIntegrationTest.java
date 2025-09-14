package com.bank.scheduler;

import com.bank.scheduler.infrastructure.web.dtos.ScheduleTransferRequest;
import com.bank.scheduler.infrastructure.web.dtos.TransferResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldScheduleAndRetrieveTransfer() throws Exception {
        // Arrange
        ScheduleTransferRequest request = new ScheduleTransferRequest();
        request.setSourceAccount("1234567890");
        request.setTargetAccount("0987654321");
        request.setAmount(new BigDecimal("1000.00"));
        request.setTransferDate(LocalDate.now());

        // Act & Assert - Schedule Transfer
        MvcResult result = mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        TransferResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            TransferResponse.class
        );

        assertNotNull(response.getId());
        assertEquals(request.getSourceAccount(), response.getSourceAccount());
        assertEquals(request.getTargetAccount(), response.getTargetAccount());
        assertEquals(0, request.getAmount().compareTo(response.getAmount()));
        assertNotNull(response.getFee());

        // Act & Assert - Retrieve Transfers
        result = mockMvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andReturn();

        TransferResponse[] transfers = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            TransferResponse[].class
        );

        assertTrue(transfers.length > 0);
        assertEquals(response.getId(), transfers[0].getId());
    }
}

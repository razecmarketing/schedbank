package com.bank.scheduler.infrastructure.web.controllers;

import com.bank.scheduler.application.usecases.TransferSchedulerService;
import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.valueobjects.Money;
import com.bank.scheduler.infrastructure.web.dtos.ScheduleTransferRequest;
import com.bank.scheduler.infrastructure.web.dtos.TransferResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferSchedulerService transferScheduler;

    public TransferController(TransferSchedulerService transferScheduler) {
        this.transferScheduler = transferScheduler;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> scheduleTransfer(
            @Valid @RequestBody ScheduleTransferRequest request) {
        
        Transfer scheduledTransfer = executeTransferScheduling(request);
        TransferResponse response = TransferResponse.fromDomain(scheduledTransfer);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransferResponse>> getAllScheduledTransfers() {
        List<Transfer> transfers = transferScheduler.listScheduledTransfers();
        List<TransferResponse> responses = convertToResponseList(transfers);
        
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduledTransfer(@PathVariable UUID id) {
        transferScheduler.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearAllScheduledTransfers() {
        transferScheduler.clearAllTransfers();
        return ResponseEntity.noContent().build();
    }

    private Transfer executeTransferScheduling(ScheduleTransferRequest request) {
        return transferScheduler.scheduleTransfer(
            request.getSourceAccount(),
            request.getTargetAccount(),
            Money.of(request.getAmount()),
            request.getTransferDate()
        );
    }

    private List<TransferResponse> convertToResponseList(List<Transfer> transfers) {
        return transfers.stream()
            .map(TransferResponse::fromDomain)
            .collect(Collectors.toList());
    }
}

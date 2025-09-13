package com.bank.scheduler.application.usecases;

import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.exceptions.DomainException;
import com.bank.scheduler.domain.policies.FeePolicy;
import com.bank.scheduler.domain.ports.TransferRepository;
import com.bank.scheduler.domain.valueobjects.AccountNumber;
import com.bank.scheduler.domain.valueobjects.Money;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class TransferSchedulerService {
    private final TransferRepository transferRepository;
    private final Set<FeePolicy> feePolicies;

    public TransferSchedulerService(TransferRepository transferRepository, Set<FeePolicy> feePolicies) {
        this.transferRepository = transferRepository;
        this.feePolicies = feePolicies;
    }

    public Transfer scheduleTransfer(
            String sourceAccountNumber,
            String targetAccountNumber,
            Money amount,
            LocalDate transferDate) {
        
        validateTransferParameters(sourceAccountNumber, targetAccountNumber, amount, transferDate);
        
        AccountNumber sourceAccount = createAccountNumber(sourceAccountNumber);
        AccountNumber targetAccount = createAccountNumber(targetAccountNumber);
        LocalDate scheduleDate = getCurrentDate();

        Money calculatedFee = calculateTransferFee(amount, scheduleDate, transferDate);

        Transfer scheduledTransfer = createTransfer(
            sourceAccount, 
            targetAccount, 
            amount, 
            calculatedFee, 
            scheduleDate, 
            transferDate
        );

        return persistTransfer(scheduledTransfer);
    }

    public List<Transfer> listScheduledTransfers() {
        return transferRepository.findAll();
    }

    public void deleteTransfer(UUID transferId) {
        validateTransferIdForDeletion(transferId);
        ensureTransferExists(transferId);
        transferRepository.deleteById(transferId);
    }

    public void clearAllTransfers() {
        transferRepository.deleteAll();
    }

    private void validateTransferParameters(String sourceAccount, String targetAccount, Money amount, LocalDate transferDate) {
        if (sourceAccount == null || targetAccount == null) {
            throw new DomainException.InvalidTransferData("Account numbers cannot be null");
        }
        if (amount == null) {
            throw new DomainException.InvalidTransferData("Transfer amount cannot be null");
        }
        if (transferDate == null) {
            throw new DomainException.InvalidTransferData("Transfer date cannot be null");
        }
    }

    private void validateTransferIdForDeletion(UUID transferId) {
        if (transferId == null) {
            throw new DomainException.InvalidTransferData("Transfer ID cannot be null");
        }
    }

    private void ensureTransferExists(UUID transferId) {
        if (!transferRepository.existsById(transferId)) {
            throw new DomainException.TransferNotFound("Transfer with ID " + transferId + " not found");
        }
    }

    private AccountNumber createAccountNumber(String accountNumber) {
        return AccountNumber.of(accountNumber);
    }

    private LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    private Money calculateTransferFee(Money amount, LocalDate scheduleDate, LocalDate transferDate) {
        return findApplicableFeePolicy(scheduleDate, transferDate)
            .calculateFee(amount, scheduleDate, transferDate);
    }

    private FeePolicy findApplicableFeePolicy(LocalDate scheduleDate, LocalDate transferDate) {
        return feePolicies.stream()
            .filter(policy -> policy.isApplicable(scheduleDate, transferDate))
            .findFirst()
            .orElseThrow(() -> new DomainException.NoApplicableFeePolicy(
                "No applicable fee policy for the provided dates"));
    }

    private Transfer createTransfer(AccountNumber source, AccountNumber target, Money amount, Money fee, LocalDate scheduleDate, LocalDate transferDate) {
        return Transfer.schedule(source, target, amount, fee, scheduleDate, transferDate);
    }

    private Transfer persistTransfer(Transfer transfer) {
        return transferRepository.save(transfer);
    }
}

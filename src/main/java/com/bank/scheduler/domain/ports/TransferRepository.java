package com.bank.scheduler.domain.ports;

import com.bank.scheduler.domain.entities.Transfer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {
    Transfer save(Transfer transfer);
    Optional<Transfer> findById(UUID id);
    List<Transfer> findAll();
    void deleteById(UUID id);
    void deleteAll();
    boolean existsById(UUID id);
}

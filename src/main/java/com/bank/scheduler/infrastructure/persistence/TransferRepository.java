package com.bank.scheduler.infrastructure.persistence;

import com.bank.scheduler.domain.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    // Métodos customizados podem ser adicionados aqui
}

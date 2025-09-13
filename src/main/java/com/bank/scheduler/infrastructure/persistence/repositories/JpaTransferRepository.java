package com.bank.scheduler.infrastructure.persistence.repositories;

import com.bank.scheduler.domain.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaTransferRepository extends JpaRepository<Transfer, UUID> {
}

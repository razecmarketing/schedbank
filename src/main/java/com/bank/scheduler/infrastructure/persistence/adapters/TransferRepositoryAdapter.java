package com.bank.scheduler.infrastructure.persistence.adapters;

import com.bank.scheduler.domain.entities.Transfer;
import com.bank.scheduler.domain.ports.TransferRepository;
import com.bank.scheduler.infrastructure.persistence.repositories.JpaTransferRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransferRepositoryAdapter implements TransferRepository {
    
    private final JpaTransferRepository jpaRepository;
    
    public TransferRepositoryAdapter(JpaTransferRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Transfer save(Transfer transfer) {
        return jpaRepository.save(transfer);
    }
    
    @Override
    public Optional<Transfer> findById(UUID id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public List<Transfer> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}

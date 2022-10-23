package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}


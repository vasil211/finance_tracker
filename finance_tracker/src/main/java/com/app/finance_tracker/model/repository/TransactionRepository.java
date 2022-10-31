package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findAllByAccountId(long accountId);

    List<Transaction> findAllByAccountIdAndCreatedAtAfterAndCreatedAtBefore(long accountId, Date from, Date to);

    void deleteAllByAccountId(long accountId);
}

package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {


    List<Transfer> findAllByReceiverIdAndSenderId(long receiverId, long senderId);
    List<Transfer> findAllByReceiverId(long accountId);
    List<Transfer> findAllBySenderId(long accountId);

    void deleteAllBySenderIdAndReceiverId(long senderId, long receiverId);
}


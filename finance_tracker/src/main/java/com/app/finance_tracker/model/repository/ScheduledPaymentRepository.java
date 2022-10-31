package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.model.entities.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment,Long> {

    List<ScheduledPayment> findAllByAccountId(long accountId);

    void deleteAllByAccountId(long accountId);
}

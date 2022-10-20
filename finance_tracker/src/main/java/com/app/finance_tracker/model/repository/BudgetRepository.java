package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {

    List<Budget> findAllByUserId(long userId);
}

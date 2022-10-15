package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget,Long> {
}

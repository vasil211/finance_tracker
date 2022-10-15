package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}

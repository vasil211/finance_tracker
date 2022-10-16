package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Icon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IconRepository extends JpaRepository<Icon,Long> {
}


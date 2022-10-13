package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}


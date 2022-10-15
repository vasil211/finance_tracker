package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Object> findByEmail(String email);

    Optional<Object> findByUsername(String username);
}


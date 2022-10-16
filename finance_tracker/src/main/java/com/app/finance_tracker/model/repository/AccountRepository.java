package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUserId(Long id);

    @Modifying
    @Query("Update Account a set a.name = ?1, a.currency.id = ?2 where a.id = ?3")
    void updateAccount(String name, long id, long id1);

    @Modifying
    @Query("Update Account a set a.balance = ?1 where a.id = ?2")
    void addMoney(double amount, long id);
}

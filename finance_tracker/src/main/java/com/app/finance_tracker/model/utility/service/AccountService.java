package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.AccountCreateDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    public Account setFields(AccountCreateDTO accountDTO) {
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setUser(userRepository.findById(accountDTO.getUserId())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid user id")));
        account.setCurrency(currencyRepository.findById(accountDTO.getCurrencyId())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid currency id")));
        return account;
    }

}

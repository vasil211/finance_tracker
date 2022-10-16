package com.app.finance_tracker.model.utility.validation;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.AccountCreateDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountValidation {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    public boolean validateName(String name) {
        return name.matches("^[A-Za-z\\s]{2,20}$");
    }


    public void validateAccountForCreation(AccountCreateDTO accountDTO) {
        if(!validateName(accountDTO.getName())) {
            throw new InvalidArgumentsException("Invalid name");
        }
    }

    public Account validateAccountForUpdate(AccountCreateDTO accountDTO) {
        if(!validateName(accountDTO.getName())) {
            throw new InvalidArgumentsException("Invalid name");
        }
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setId(accountDTO.getId());
        account.setCurrency(currencyRepository.findById(accountDTO.getCurrencyId())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid currency id")));
        return account;
    }

    public long validateId(String id) {
        long accountId;
        try {
            accountId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Invalid id");
        }
        return accountId;
    }

    public void validateMoneyAmount(double amount) {
        if(amount <= 0) {
            throw new InvalidArgumentsException("Invalid amount");
        }
    }
}

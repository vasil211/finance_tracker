package com.app.finance_tracker.service;

import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountCreateDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForUpdateDTO;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService extends AbstractService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CurrencyExchangeService currencyExchangeService;
    public AccountForReturnDTO addAccount(AccountCreateDTO accountDTO, long userId) {
        accountValidation.validateAccountForCreation(accountDTO);
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setUser(getUserById(userId));
        account.setCurrency(getCurrencyById(accountDTO.getCurrencyId()));
        accountRepository.save(account);
        AccountForReturnDTO accountForReturnDTO = modelMapper.map(account, AccountForReturnDTO.class);
        accountForReturnDTO.setCurrency(modelMapper.map(account.getCurrency(), CurrencyForReturnDTO.class));
        return accountForReturnDTO;
    }

    public List<AccountForReturnDTO> getAllAccountsForUser(long id) {
        List<AccountForReturnDTO> accounts = accountRepository.findAllByUserId(id).stream()
                .map(account -> {
                    AccountForReturnDTO accountForReturnDTO = modelMapper.map(account, AccountForReturnDTO.class);
                    accountForReturnDTO.setCurrency(modelMapper.map(account.getCurrency(), CurrencyForReturnDTO.class));
                    return accountForReturnDTO;
                }).toList();
        return accounts;
    }

    public AccountForReturnDTO updateAccount(AccountForUpdateDTO accountDTO, long userId) {
        checkIfAccountBelongsToUser(accountDTO.getId(), userId);
        accountValidation.validateAccountForUpdate(accountDTO);
        Account account = getAccountById(accountDTO.getId());
        if(!account.getName().equals(accountDTO.getName())){
            account.setName(accountDTO.getName());
        }
        if(accountDTO.getCurrencyId() != account.getCurrency().getId()){
            Currency currency = getCurrencyById(accountDTO.getCurrencyId());
            double  amount = currencyExchangeService.getExchangedCurrency(account.getCurrency().getCode(),
                    currency.getCode(), account.getBalance()).getResult();
            account.setBalance(amount);
            account.setCurrency(currency);
        }
        accountRepository.save(account);
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public AccountForReturnDTO addMoneyToAccount(AccountAddMoneyDTO accountDTO, long userId) {
        checkIfAccountBelongsToUser(accountDTO.getId(), userId);
        Account account = getAccountById(accountDTO.getId());
        accountValidation.validateMoneyAmount(accountDTO.getAmount());
        account.setBalance(account.getBalance() + accountDTO.getAmount());
        accountRepository.save(account);
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public AccountForReturnDTO getAccount(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public void checkIfAccountBelongsToUser(long postId, long userId) {
        Account account = getAccountById(postId);
        if(account.getUser().getId() != userId) {
            throw new InvalidArgumentsException("Account does not belong to user");
        }
    }

}

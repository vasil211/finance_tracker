package com.app.finance_tracker.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountCreateDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForUpdateDTO;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import com.app.finance_tracker.model.entities.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService extends AbstractService {

    public AccountForReturnDTO addAccount(AccountCreateDTO accountDTO) {
        accountValidation.validateAccountForCreation(accountDTO);
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setUser(getUserById(accountDTO.getUserId()));
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

    public AccountForReturnDTO updateAccount(AccountForUpdateDTO accountDTO) {
        accountValidation.validateAccountForUpdate(accountDTO);
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setCurrency(currencyRepository.findById(accountDTO.getCurrencyId())
                .orElseThrow(() -> new BadRequestException("Invalid currency id")));
        accountRepository.save(account);
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public AccountForReturnDTO addMoneyToAccount(AccountAddMoneyDTO accountDTO) {
        Account account = accountRepository.findById(accountDTO.getId())
                .orElseThrow(() -> new BadRequestException("Invalid account id"));
        accountValidation.validateMoneyAmount(accountDTO.getAmount());
        account.setBalance(account.getBalance() + accountDTO.getAmount());
        accountRepository.save(account);
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public AccountForReturnDTO getAccount(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Invalid account id"));
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public MessageDTO deleteAccount(long id, long userId) {
        checkIfAccountBelongsToUser(id,userId);
        Account account = getAccountById(id);
        accountRepository.delete(account);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage("Account deleted successfully");
        return messageDTO;
    }

    public void checkIfAccountBelongsToUser(long postId, long userId) {
        Account account = getAccountById(postId);
        if(account.getUser().getId() != userId) {
            throw new InvalidArgumentsException("Account does not belong to user");
        }
    }

}

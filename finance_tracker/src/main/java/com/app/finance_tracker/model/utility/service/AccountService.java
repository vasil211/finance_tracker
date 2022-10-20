package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountCreateDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForUpdateDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.AccountValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private AccountValidation accountValidation;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AccountRepository accountRepository;

    public AccountForReturnDTO addAccount(AccountCreateDTO accountDTO) {
        accountValidation.validateAccountForCreation(accountDTO);
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setUser(userRepository.findById(accountDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found")));
        account.setCurrency(currencyRepository.findById(accountDTO.getCurrencyId())
                .orElseThrow(() -> new BadRequestException("Invalid currency")));
        accountRepository.save(account);
        return modelMapper.map(account, AccountForReturnDTO.class);
    }

    public List<AccountForReturnDTO> getAllAccountsForUser(long id) {
        return accountRepository.findAllByUserId(id).stream()
                .map(account -> modelMapper.map(account, AccountForReturnDTO.class))
                .toList();
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

    public MessageDTO deleteAccount(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Invalid account id"));
        accountRepository.delete(account);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage("Account deleted successfully");
        return messageDTO;
    }
}

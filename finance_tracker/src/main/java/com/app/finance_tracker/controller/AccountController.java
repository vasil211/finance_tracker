package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.AccountCreateDTO;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.AccountValidation;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController extends MasterControllerForExceptionHandlers {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountValidation accountValidation;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/getAllAccounts/{id}")
    public ResponseEntity<List<Account>> getAllAccountsForUser(@PathVariable String id) {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Invalid id");
        }
        return ResponseEntity.ok(accountRepository.findAllByUserId(userId));
    }

    @PostMapping("/addAccount")
    public ResponseEntity<Account> addAccount(@RequestBody AccountCreateDTO accountDTO) {
        Account account = accountValidation.validateAccountForCreation(accountDTO);
        accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    @Transactional
    @PutMapping("/updateAccount")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountCreateDTO accountDTO) {
        Account account = accountValidation.validateAccountForUpdate(accountDTO);
        accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    @Transactional
    @PutMapping("/addMoneyToAccount")
    public ResponseEntity<Account> addMoneyToAccount(@RequestBody AccountAddMoneyDTO accountDTO) {
        Account account = accountRepository.findById(accountDTO.getId())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid account id"));
        accountValidation.validateMoneyAmount(accountDTO.getAmount());
        account.setBalance(account.getBalance() + accountDTO.getAmount());
        accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/getAccount/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable String id) {
        long accountId;
        try {
            accountId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Invalid id");
        }
        return ResponseEntity.ok(accountRepository.findById(accountId)
                .orElseThrow(() -> new InvalidArgumentsException("Invalid account id")));
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<MessageDTO> deleteAccount(@PathVariable String id) {
        long accountId = accountValidation.validateId(id);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new InvalidArgumentsException("Invalid account id"));
        accountRepository.delete(account);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage("Account deleted successfully");
        return ResponseEntity.status(200).body(messageDTO);
    }
}

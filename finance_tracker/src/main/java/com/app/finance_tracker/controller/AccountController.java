package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.AccountCreateDTO;
import com.app.finance_tracker.model.dto.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.service.AccountService;
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
    @Autowired
    private AccountService accountService;


    @GetMapping("/getAllAccounts/{id}")
    public ResponseEntity<List<AccountForReturnDTO>> getAllAccountsForUser(@PathVariable long id) {
        List<AccountForReturnDTO> dtoAccounts = accountRepository.findAllByUserId(id).stream()
                .map(account -> modelMapper.map(account, AccountForReturnDTO.class))
                .toList();
        return ResponseEntity.ok(dtoAccounts);
    }

    @PostMapping("/addAccount")
    public ResponseEntity<AccountForReturnDTO> addAccount(@RequestBody AccountCreateDTO accountDTO) {
        accountValidation.validateAccountForCreation(accountDTO);
        Account account = accountService.setFields(accountDTO);
        accountRepository.save(account);
        return ResponseEntity.ok(modelMapper.map(account, AccountForReturnDTO.class));
    }

    @Transactional
    @PutMapping("/updateAccount")
    public ResponseEntity<AccountForReturnDTO> updateAccount(@RequestBody AccountCreateDTO accountDTO) {
        Account account = accountValidation.validateAccountForUpdate(accountDTO);
        accountRepository.save(account);
        return ResponseEntity.ok(modelMapper.map(account, AccountForReturnDTO.class));
    }

    @Transactional
    @PutMapping("/addMoneyToAccount")
    public ResponseEntity<AccountForReturnDTO> addMoneyToAccount(@RequestBody AccountAddMoneyDTO accountDTO) {
        Account account = accountRepository.findById(accountDTO.getId())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid account id"));
        accountValidation.validateMoneyAmount(accountDTO.getAmount());
        account.setBalance(account.getBalance() + accountDTO.getAmount());
        accountRepository.save(account);
        return ResponseEntity.ok(modelMapper.map(account, AccountForReturnDTO.class));
    }

    @GetMapping("/getAccount/{id}")
    public ResponseEntity<AccountForReturnDTO> getAccount(@PathVariable long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentsException("Invalid account id"));

        return ResponseEntity.ok(modelMapper.map(account, AccountForReturnDTO.class));
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<MessageDTO> deleteAccount(@PathVariable long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentsException("Invalid account id"));
        accountRepository.delete(account);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage("Account deleted successfully");
        return ResponseEntity.status(200).body(messageDTO);
    }
}

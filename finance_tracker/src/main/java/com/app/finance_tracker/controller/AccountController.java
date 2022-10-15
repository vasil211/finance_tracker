package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.dto.AccountCreateDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.AccountValidation;
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
            throw new BadRequestException("Invalid id");
        }
        return ResponseEntity.ok(accountRepository.findAllByUserId(userId));
    }

    @PostMapping("/addAccount")
    public ResponseEntity<Account> addAccount(@RequestBody AccountCreateDTO accountDTO) {
        if(!accountValidation.validateName(accountDTO.getName())) {
            throw new BadRequestException("Invalid name");
        }
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setUser(userRepository.findById(accountDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("Invalid user id")));
        account.setCurrency(currencyRepository.findById(accountDTO.getCurrencyId())
                .orElseThrow(() -> new BadRequestException("Invalid currency id")));
        accountRepository.save(account);
        return ResponseEntity.ok(account);
    }
}

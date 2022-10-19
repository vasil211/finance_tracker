package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.accountDTO.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountCreateDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForUpdateDTO;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class AccountController extends MasterControllerForExceptionHandlers {

    @Autowired
    private AccountService accountService;


    @PostMapping("/addAccount")
    public ResponseEntity<AccountForReturnDTO> addAccount(@RequestBody AccountCreateDTO accountDTO) {
        // todo check if user is logged in
        AccountForReturnDTO accountForReturnDTO = accountService.addAccount(accountDTO);
        return ResponseEntity.ok(accountForReturnDTO);
    }

    @GetMapping("/getAllAccounts/{id}")
    public ResponseEntity<List<AccountForReturnDTO>> getAllAccountsForUser(@PathVariable long id) {
        // todo check if user is logged in, and remove id from path
        List<AccountForReturnDTO> accountsDTO = accountService.getAllAccountsForUser(id);
        return ResponseEntity.ok(accountsDTO);
    }


    @Transactional
    @PutMapping("/updateAccount")
    public ResponseEntity<AccountForReturnDTO> updateAccount(@RequestBody AccountForUpdateDTO accountDTO) {
        // todo check if user is logged in
        AccountForReturnDTO account = accountService.updateAccount(accountDTO);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/addMoneyToAccount")
    public ResponseEntity<AccountForReturnDTO> addMoneyToAccount(@RequestBody AccountAddMoneyDTO accountDTO) {
        // todo check if user is logged in
        AccountForReturnDTO account = accountService.addMoneyToAccount(accountDTO);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/getAccount/{id}")
    public ResponseEntity<AccountForReturnDTO> getAccount(@PathVariable long id) {
        // todo check if user is logged in and check if user is owner of account
        AccountForReturnDTO account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<MessageDTO> deleteAccount(@PathVariable long id) {
        // todo check if user is logged in and check if user is owner of account
        MessageDTO message = accountService.deleteAccount(id);
        return ResponseEntity.ok(message);
    }
}

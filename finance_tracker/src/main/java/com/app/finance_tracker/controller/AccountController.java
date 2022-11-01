package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.accountDTO.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountCreateDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForUpdateDTO;
import com.app.finance_tracker.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class AccountController extends AbstractController {

    @Autowired
    private AccountService accountService;


    @PostMapping("/accounts")
    public ResponseEntity<AccountForReturnDTO> addAccount(@RequestBody AccountCreateDTO accountDTO, HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        AccountForReturnDTO accountForReturnDTO = accountService.addAccount(accountDTO, userId);
        return ResponseEntity.ok(accountForReturnDTO);
    }

    @GetMapping("/accounts/all")
    public ResponseEntity<List<AccountForReturnDTO>> getAllAccountsForUser(HttpServletRequest request) {
        long id = checkIfLoggedAndReturnUserId(request);
        List<AccountForReturnDTO> accountsDTO = accountService.getAllAccountsForUser(id);
        return ResponseEntity.ok(accountsDTO);
    }

    @PutMapping("/accounts")
    public ResponseEntity<AccountForReturnDTO> updateAccount(@RequestBody AccountForUpdateDTO accountDTO, HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        AccountForReturnDTO account = accountService.updateAccount(accountDTO, userId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/accounts/fill")
    public ResponseEntity<AccountForReturnDTO> addMoneyToAccount(@RequestBody AccountAddMoneyDTO accountDTO, HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        AccountForReturnDTO account = accountService.addMoneyToAccount(accountDTO, userId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountForReturnDTO> getAccount(@PathVariable long id, HttpServletRequest request) {
        long userId= checkIfLoggedAndReturnUserId(request);
        accountService.checkIfAccountBelongsToUser(id, userId);
        AccountForReturnDTO account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<MessageDTO> deleteAccount(@PathVariable long id, HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        MessageDTO message = accountService.deleteAccount(id, userId);
        return ResponseEntity.ok(message);
    }



}

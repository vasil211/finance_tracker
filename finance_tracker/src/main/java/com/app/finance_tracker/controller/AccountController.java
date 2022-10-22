package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.accountDTO.AccountAddMoneyDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountCreateDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.accountDTO.AccountForUpdateDTO;
import com.app.finance_tracker.service.AccountService;
import com.app.finance_tracker.service.UserService;
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


    @PostMapping("/account")
    public ResponseEntity<AccountForReturnDTO> addAccount(@RequestBody AccountCreateDTO accountDTO, HttpServletRequest request) {
        checkIfLogged(request);
        AccountForReturnDTO accountForReturnDTO = accountService.addAccount(accountDTO);
        return ResponseEntity.ok(accountForReturnDTO);
    }

    @GetMapping("/account/all")
    public ResponseEntity<List<AccountForReturnDTO>> getAllAccountsForUser(HttpServletRequest request) {
        long id = checkIfLoggedAndReturnUserId(request);
        List<AccountForReturnDTO> accountsDTO = accountService.getAllAccountsForUser(id);
        return ResponseEntity.ok(accountsDTO);
    }

    @PutMapping("/account")
    public ResponseEntity<AccountForReturnDTO> updateAccount(@RequestBody AccountForUpdateDTO accountDTO, HttpServletRequest request) {
        checkIfLogged(request);
        checkIfAccountBelongsToUser(accountDTO.getId(), request);
        AccountForReturnDTO account = accountService.updateAccount(accountDTO);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/account/fill")
    public ResponseEntity<AccountForReturnDTO> addMoneyToAccount(@RequestBody AccountAddMoneyDTO accountDTO, HttpServletRequest request) {
        checkIfLogged(request);
        checkIfAccountBelongsToUser(accountDTO.getId(), request);
        AccountForReturnDTO account = accountService.addMoneyToAccount(accountDTO);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountForReturnDTO> getAccount(@PathVariable long id, HttpServletRequest request) {
        checkIfLogged(request);
        accountService.checkIfAccountBelongsToUser(id, Long.parseLong(request.getParameter(USER_ID)));
        AccountForReturnDTO account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/account/{id}")
    public ResponseEntity<MessageDTO> deleteAccount(@PathVariable long id, HttpServletRequest request) {
        checkIfLogged(request);
        accountService.checkIfAccountBelongsToUser(id, Long.parseLong(request.getParameter(USER_ID)));
        MessageDTO message = accountService.deleteAccount(id);
        return ResponseEntity.ok(message);
    }



}

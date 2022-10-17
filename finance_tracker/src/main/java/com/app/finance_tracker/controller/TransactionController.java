package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.CreateTransactionDto;
import com.app.finance_tracker.model.dto.TransactionReturnDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.TransactionRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/accounts/{id}/create_transaction")
    public ResponseEntity<TransactionReturnDto> createTransaction(@RequestBody CreateTransactionDto transactionDto, @PathVariable long id){

        if (!accountRepository.existsById(id)){
            throw new BadRequestException("no such account found!");
        }

        Account account = accountRepository.findById(id).get();
        if (checkBalance(account,transactionDto.getAmount())){
            throw new BadRequestException("Not enough money. Please add money to the account to make this transaction");
        }
        Transaction transaction = transactionService.setFields(transactionDto,account);
        transaction.setAccount(account);
        account.removeFromBalance(transaction.getAmount());
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return new ResponseEntity<>(modelMapper.map(transaction, TransactionReturnDto.class), HttpStatus.CREATED);
    }

    //transactions for user
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAllTransactions(@PathVariable long userId){
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        List<TransactionReturnDto> transactions = transactionRepository.findAll()
                .stream().filter(t -> t.getAccount().getUser().getId()==userId)
                .map(t-> modelMapper.map(t,TransactionReturnDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    //transaction by id
    @GetMapping("/{userId}/transactions/{id}")
    public ResponseEntity<TransactionReturnDto> getTransactionById(@PathVariable long userId, @PathVariable long id){
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        if (!transactionRepository.existsById(id)){
            throw new NotFoundException("not found");
        }
        Transaction transaction = transactionRepository.findById(id).get();
        return new ResponseEntity<>(modelMapper.map(transaction,TransactionReturnDto.class),HttpStatus.OK);
    }
    //Getting all transactions for account
    @GetMapping("{userId}/accounts{accountId}/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAccountTransactions(@PathVariable long userId, @PathVariable long accountId){
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found");
        }
        if (!accountRepository.existsById(accountId)){
            throw new NotFoundException("account not found");
        }
        List<TransactionReturnDto> list = transactionRepository.findAllById(accountId)
                .stream().map(t -> modelMapper.map(t,TransactionReturnDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    private boolean checkBalance(Account account, double amount){
        return account.getBalance() < amount;
    }

}

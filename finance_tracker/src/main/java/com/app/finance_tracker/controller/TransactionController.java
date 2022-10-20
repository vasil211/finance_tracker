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
public class TransactionController extends MasterControllerForExceptionHandlers{

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/accounts/{id}/create_transaction")
    public ResponseEntity<TransactionReturnDto> createTransaction(@RequestBody CreateTransactionDto transactionDto, @PathVariable long id){
        TransactionReturnDto transaction = transactionService.createTransaction(transactionDto,id);
        return new ResponseEntity<>(modelMapper.map(transaction, TransactionReturnDto.class), HttpStatus.CREATED);
    }

    //transactions for user
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAllTransactions(@PathVariable long userId){
        List<TransactionReturnDto> transactions = transactionService.getAllByUserId(userId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    //transaction by id
    @GetMapping("/{userId}/transactions/{id}")
    public ResponseEntity<TransactionReturnDto> getTransactionById(@PathVariable long userId, @PathVariable long id){
        TransactionReturnDto transaction = transactionService.getTransactionById(userId,id);
        return new ResponseEntity<>(modelMapper.map(transaction,TransactionReturnDto.class),HttpStatus.OK);
    }
    //Getting all transactions for account
    @GetMapping("{userId}/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAccountTransactions(@PathVariable long userId, @PathVariable long accountId){
        List<TransactionReturnDto> transactions =transactionService.getAllTransactionsForAccount(userId,accountId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

}

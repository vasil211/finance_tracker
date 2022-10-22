package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.transactionDTO.CreateTransactionDto;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionReturnDto;
import com.app.finance_tracker.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController extends AbstractController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/transactions")
    public ResponseEntity<TransactionReturnDto> createTransaction(@RequestBody CreateTransactionDto transactionDto,
                                                                  @PathVariable long id, HttpServletRequest req){
        checkIfLogged(req);
        checkIfAccountBelongsToUser(id,req);
        TransactionReturnDto transaction = transactionService.createTransaction(transactionDto,id);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    //transactions for user
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAllTransactions(HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransactionReturnDto> transactions = transactionService.getAllByUserId(userId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    //transaction by id
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionReturnDto> getTransactionById(@PathVariable long id, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        TransactionReturnDto transaction = transactionService.getTransactionById(userId,id);
        return new ResponseEntity<>(modelMapper.map(transaction,TransactionReturnDto.class),HttpStatus.OK);
    }
    //Getting all transactions for account
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAccountTransactions(@PathVariable long accountId, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        checkIfAccountBelongsToUser(accountId,request);
        List<TransactionReturnDto> transactions =transactionService.getAllTransactionsForAccount(userId,accountId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

}

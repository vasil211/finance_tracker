package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.transactionDTO.CreateTransactionDto;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionFilteredDto;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionReturnDto;
import com.app.finance_tracker.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                                                                  HttpServletRequest req){
       long userId = checkIfLoggedAndReturnUserId(req);
        TransactionReturnDto transaction = transactionService.createTransaction(transactionDto,transactionDto.getAccountId(), userId);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAllTransactions(HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransactionReturnDto> transactions = transactionService.getAllForUser(userId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionReturnDto> getTransactionById(@PathVariable long id, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        TransactionReturnDto transaction = transactionService.getTransactionById(userId,id);
        return new ResponseEntity<>(modelMapper.map(transaction,TransactionReturnDto.class),HttpStatus.OK);
    }

    @GetMapping("/accounts/transactions")
    public ResponseEntity<List<TransactionReturnDto>> getAccountTransactions(@RequestParam long accountId,
                                                                             HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransactionReturnDto> transactions =transactionService.getAllTransactionsForAccount(userId,accountId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    @PostMapping("/transactions/filtered")
    public ResponseEntity<List<TransactionReturnDto>> getFilteredTransactions(@RequestBody TransactionFilteredDto transactionFilteredDto,
                                                                              HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransactionReturnDto> list = transactionService.getFilteredTransactions(userId,transactionFilteredDto);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PostMapping("/transactions/downloadPDF")
    public ResponseEntity<String> downloadTransactionsPdf(@RequestBody TransactionFilteredDto filteredDto,
                                                          HttpServletRequest request, HttpServletResponse response){
        long userId = checkIfLoggedAndReturnUserId(request);
        transactionService.downloadTransactionsPdf(userId,filteredDto,response);
        return new ResponseEntity<>("Download successful",HttpStatus.OK);
    }
}

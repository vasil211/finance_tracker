package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.CreateBudgetDto;
import com.app.finance_tracker.model.dto.CreateTransactionDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.TransactionRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/accounts/{id}/create_transaction")
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionDto transactionDto, @PathVariable long id){

        if (!accountRepository.existsById(id)){
            throw new BadRequestException("no account found!");
        }
        if (!validateCategory(transactionDto.getCategoryId())){
            throw new NotFoundException("Invalid category");
        }
        Account account = accountRepository.findById(id).get();
        if (checkBalance(account,transactionDto.getAmount())){
            throw new BadRequestException("Not enough money. Please add money to the account to make this transaction");
        }

        Category category= categoryRepository.findById(id).get();
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCategory(category);
        transaction.setDescription(transactionDto.getDescription());
        transaction.setAccount(account);
        transaction.setCreatedAt(Date.from(Instant.now()));
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    //transactions for user
    @GetMapping("/{userId}/transactions/")
    public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable long userId){
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        List<Transaction> transactions = transactionRepository.findAllById(userId);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    //transaction by id
    @GetMapping("/{userId}/transactions/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long userId, @PathVariable long id){
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        if (!transactionRepository.existsById(id)){
            throw new NotFoundException("not found");
        }
        Transaction transaction = transactionRepository.findById(id).get();
        return new ResponseEntity<>(transaction,HttpStatus.OK);
    }

    private boolean checkBalance(Account account, double amount){
        return account.getBalance() < amount;
    }

    private boolean validateCategory(long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}

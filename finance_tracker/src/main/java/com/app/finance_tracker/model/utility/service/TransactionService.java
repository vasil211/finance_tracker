package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.Exeptionls.UnauthorizedException;
import com.app.finance_tracker.model.dto.CreateTransactionDto;
import com.app.finance_tracker.model.dto.TransactionReturnDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.repository.AccountRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.TransactionRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ModelMapper modelMapper;


    private boolean validateCategory(long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    public List<TransactionReturnDto> getAllTransactionsForAccount(long userId, long accountId) {
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found");
        }
        if (!accountRepository.existsById(accountId)){
            throw new NotFoundException("account not found");
        }
        List<TransactionReturnDto> list = transactionRepository.findAllById(accountId)
                .stream().map(t -> modelMapper.map(t, TransactionReturnDto.class)).toList();
        return list;
    }

    public List<TransactionReturnDto> getAllByUserId(long userId) {
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        List<TransactionReturnDto> transactions = transactionRepository.findAll()
                .stream().filter(t -> t.getAccount().getUser().getId() == userId)
                .map(t -> modelMapper.map(t, TransactionReturnDto.class)).toList();
        return transactions;
    }

    @Transactional
    public TransactionReturnDto createTransaction(CreateTransactionDto transactionDto, long id) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("account not found!"));

        if (checkBalance(account,transactionDto.getAmount())){
            throw new BadRequestException("Not enough money. Please add money to the account to make this transaction");
        }
        Transaction transaction = setFields(transactionDto);
        transaction.setAccount(account);
        account.removeFromBalance(transaction.getAmount());
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return modelMapper.map(transaction,TransactionReturnDto.class);
    }
    public Transaction setFields(CreateTransactionDto transactionDto) {

        if (!validateCategory(transactionDto.getCategoryId())){
            throw new NotFoundException("Invalid category");
        }
        Category category= categoryRepository.findById(transactionDto.getCategoryId()).get();

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCategory(category);
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCreatedAt(Date.from(Instant.now()));
        return transaction;
    }

    private boolean checkBalance(Account account, double amount){
        return account.getBalance() < amount;
    }

    public TransactionReturnDto getTransactionById(long userId, long id) {
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        Transaction transaction = transactionRepository.findById(id).orElseThrow( ()-> new NotFoundException("transaction not found!"));

        if (transaction.getAccount().getUser().getId()!= userId){
            throw new UnauthorizedException("no access to this transaction");
        }
        return modelMapper.map(transaction,TransactionReturnDto.class);
    }
}

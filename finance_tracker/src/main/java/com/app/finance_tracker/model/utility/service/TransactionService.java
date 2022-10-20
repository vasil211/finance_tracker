package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.Exeptionls.UnauthorizedException;
import com.app.finance_tracker.model.dto.transactionDTO.CreateTransactionDto;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionReturnDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService extends AbstractService{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

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
        if (!isValidAmount(transactionDto.getAmount())){
            throw new BadRequestException("money should be higher than 0");
        }

        Account account = getAccountById(id);
        Budget budget = budgetRepository.findAllByUserId(account.getUser().getId())
                .stream().filter(b->b.getCategory().getId()==transactionDto.getCategoryId())
                .findFirst().orElseThrow(() -> new BadRequestException("You dont have budget for this category."));

        if (!isBudgetBalanceEnough(transactionDto.getAmount(),budget.getAmount())){
            throw new InvalidArgumentsException("not enough budget for this category");
        }

        if (checkBalance(account,transactionDto.getAmount())){
            throw new BadRequestException("Not enough money. Please add money to the account to make this transaction");
        }
        Transaction transaction = setFields(transactionDto);
        transaction.setAccount(account);
        account.removeFromBalance(transaction.getAmount());
        budget.decreaseAmount(transactionDto.getAmount());
        transactionRepository.save(transaction);
        accountRepository.save(account);
        budgetRepository.save(budget);
        return modelMapper.map(transaction,TransactionReturnDto.class);
    }

    private boolean isBudgetBalanceEnough(double amount, double budgetBalance) {
        return budgetBalance>amount;
    }

    public Transaction setFields(CreateTransactionDto transactionDto) {

        if (!validateCategory(transactionDto.getCategoryId())){
            throw new NotFoundException("Invalid category");
        }
        Category category= getCategoryById(transactionDto.getCategoryId());

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
        Transaction transaction = findTransactionById(id);

        if (transaction.getAccount().getUser().getId()!= userId){
            throw new UnauthorizedException("no access to this transaction");
        }
        return modelMapper.map(transaction,TransactionReturnDto.class);
    }
}

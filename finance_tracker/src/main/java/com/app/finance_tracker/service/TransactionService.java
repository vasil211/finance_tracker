package com.app.finance_tracker.service;

import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyExchangeDto;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionFilteredDto;
import com.app.finance_tracker.model.entities.*;
import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.InvalidArgumentsException;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.exceptions.UnauthorizedException;
import com.app.finance_tracker.model.dto.transactionDTO.CreateTransactionDto;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionReturnDto;
import com.app.finance_tracker.model.repository.*;
import com.app.finance_tracker.model.utility.PdfGenerator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService extends AbstractService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CurrencyExchangeService currencyExchangeService;


    public List<TransactionReturnDto> getAllTransactionsForAccount(long userId, long accountId) {
        checkIfAccountBelongsToUser(userId, accountId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user not found");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("account not found");
        }
        List<TransactionReturnDto> list = transactionRepository.findAllByAccountId(accountId)
                .stream().map(t -> modelMapper.map(t, TransactionReturnDto.class)).toList();
        return list;
    }

    //TODO FIX THIS TO GET THE LIST WITH QUERY
    public List<TransactionReturnDto> getAllByUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user not found.");
        }
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(userId);
        List<TransactionReturnDto> result = transactions.stream().map(t -> modelMapper.map(t, TransactionReturnDto.class)).toList();
        return result;
    }

    /*public List<TransactionReturnDto> getAllByUserIdAfterDate(long userId, LocalDate date) {
        List<TransactionReturnDto> list = getAllByUserId(userId).stream()
                .filter(t -> t.getCreatedAt().isAfter(ChronoLocalDateTime.from(date)))
                .toList();

        return list;
    }*/

    @Transactional
    public TransactionReturnDto createTransaction(CreateTransactionDto transactionDto, long id, long userId) {
        checkIfAccountBelongsToUser(id, userId);
        if (!isValidAmount(transactionDto.getAmount())) {
            throw new BadRequestException("money should be higher than 0");
        }

        Account account = getAccountById(id);
        Budget budget = budgetRepository.findBudgetByCategoryIdAndUserId(transactionDto.getCategoryId(), userId)
                .orElseThrow(() -> new BadRequestException("You dont have budget for this category."));

        double amount = transactionDto.getAmount();

        if (checkBalance(account, amount)) {
            throw new BadRequestException("Not enough money. Please add money to the account to make this transaction");
        }

        Category category = getCategoryById(transactionDto.getCategoryId());
        if (category.getUser() != null && category.getUser().getId() != userId) {
            throw new NotFoundException("You dont have such category.");
        }

        Transaction transaction = setFields(transactionDto);
        transaction.setCategory(category);
        transaction.setAccount(account);
        account.removeFromBalance(amount);
        transactionRepository.save(transaction);
        accountRepository.save(account);

        if (account.getCurrency().getId() != budget.getCurrency().getId()) {
            Currency accountCurrency = account.getCurrency();
            Currency budgetCurrency = budget.getCurrency();
            CurrencyExchangeDto dto = currencyExchangeService.getExchangedCurrency(accountCurrency.getCode(), budgetCurrency.getCode(), amount);
            amount = dto.getResult();
        }

        budget.decreaseAmount(amount);
        budgetRepository.save(budget);
        return modelMapper.map(transaction, TransactionReturnDto.class);
    }

    public Transaction setFields(CreateTransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }

    private boolean checkBalance(Account account, double amount) {
        return account.getBalance() < amount;
    }

    public TransactionReturnDto getTransactionById(long userId, long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user not found.");
        }
        Transaction transaction = getTransactionById(id);

        if (transaction.getAccount().getUser().getId() != userId) {
            throw new UnauthorizedException("no access to this transaction");
        }
        return modelMapper.map(transaction, TransactionReturnDto.class);
    }

    public List<TransactionReturnDto> getFilteredTransactions(long userId, TransactionFilteredDto transactionFilteredDto) {
        List<Long> ownAccounts = transactionFilteredDto.getAccountIds();
        List<Long> wantedCategories = transactionFilteredDto.getCategoryIds();
        if (ownAccounts.size() == 0) {
            ownAccounts = accountRepository.findAllByUserId(userId).stream().map(a -> a.getId()).toList();
        }
        else {
            if (ownAccounts.stream().anyMatch(a -> getAccountById(a).getUser().getId() != userId))
                throw new UnauthorizedException("Dont have access for this action");
        }

        List<Transaction> transactions = transactionDAO.testFilter(ownAccounts, transactionFilteredDto.getFromAmount(),
                transactionFilteredDto.getToAmount(), transactionFilteredDto.getFromDate(),
                transactionFilteredDto.getToDate(), wantedCategories);

        List<TransactionReturnDto> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            result.add(modelMapper.map(transaction, TransactionReturnDto.class));
        }
        return result;
    }

    public void downloadTransactionsPdf(long userId, TransactionFilteredDto filteredDto, HttpServletResponse response) {
        List<TransactionReturnDto> transactions = getFilteredTransactions(userId, filteredDto);
        Map<CurrencyForReturnDTO, Double> totalAmounts = new HashMap<>();
        for (TransactionReturnDto transaction : transactions) {
            if (totalAmounts.containsKey(transaction.getAccount().getCurrency())) {
                totalAmounts.put(transaction.getAccount().getCurrency(),
                        totalAmounts.get(transaction.getAccount().getCurrency()) + transaction.getAmount());
            } else {
                totalAmounts.put(transaction.getAccount().getCurrency(), transaction.getAmount());
            }
        }
        PdfGenerator<TransactionReturnDto> generator = new PdfGenerator<>();
        generator.generatePdfFile(transactions, response, totalAmounts);
    }
}

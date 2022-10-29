package com.app.finance_tracker.service;

import com.app.finance_tracker.model.dto.transactionDTO.TransactionFilteredDto;
import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.InvalidArgumentsException;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.exceptions.UnauthorizedException;
import com.app.finance_tracker.model.dto.transactionDTO.CreateTransactionDto;
import com.app.finance_tracker.model.dto.transactionDTO.TransactionReturnDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.repository.*;
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
        checkIfAccountBelongsToUser(userId, accountId);
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found");
        }
        if (!accountRepository.existsById(accountId)){
            throw new NotFoundException("account not found");
        }
        List<TransactionReturnDto> list = transactionRepository.findAllByAccountId(accountId)
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

    public List<TransactionReturnDto> getAllByUserIdAfterDate(long userId, LocalDate date) {
        List<TransactionReturnDto> list = getAllByUserId(userId).stream()
                .filter(t -> t.getCreatedAt().isAfter(ChronoLocalDateTime.from(date)))
                .toList();

        return list;
    }

    @Transactional
    public TransactionReturnDto createTransaction(CreateTransactionDto transactionDto, long id, long userId) {
        checkIfAccountBelongsToUser(id, userId);
        if (!isValidAmount(transactionDto.getAmount())){
            throw new BadRequestException("money should be higher than 0");
        }

        Account account = getAccountById(id);
        //TODO Refactor budget later
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
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }

    private boolean checkBalance(Account account, double amount){
        return account.getBalance() < amount;
    }

    public TransactionReturnDto getTransactionById(long userId, long id) {
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("user not found.");
        }
        Transaction transaction = getTransactionById(id);

        if (transaction.getAccount().getUser().getId()!= userId){
            throw new UnauthorizedException("no access to this transaction");
        }
        return modelMapper.map(transaction,TransactionReturnDto.class);
    }

    public List<TransactionReturnDto> getFilteredTransactions(long userId, TransactionFilteredDto transactionFilteredDto) {
        List<Long> ownAccounts = transactionFilteredDto.getAccountIds();
        List<Long> wantedCategories = transactionFilteredDto.getCategoryIds();
        if (ownAccounts.size()==0){
            ownAccounts= accountRepository.findAllByUserId(userId).stream().map(a -> a.getId()).toList();
        }
        /*if (wantedCategories.size()==0){
            wantedCategories = categoryRepository.findAllByUserIsNullOrUserId(userId).stream().map(c -> c.getId()).toList();
        }*/
        List<Transaction> transactions = transactionDAO.getFilteredTransactions(ownAccounts,transactionFilteredDto.getFromAmount(),
                transactionFilteredDto.getToAmount(),transactionFilteredDto.getFromDate(),
                transactionFilteredDto.getToDate(),wantedCategories);

        List<TransactionReturnDto> result = new ArrayList<>();
        for (Transaction transaction : transactions){
            result.add(modelMapper.map(transaction,TransactionReturnDto.class));
        }
        return result;
    }
    @SneakyThrows
    public byte[] exportPdf(long userId, TransactionFilteredDto filteredDto, HttpServletResponse response) {
        List<TransactionReturnDto> transactions = getFilteredTransactions(userId,filteredDto);
        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream("dataOutput.pdf"));
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA,14, BaseColor.BLACK);
        for (TransactionReturnDto data: transactions) {
            String str = data.toString();
            document.add(new Paragraph("\n"));
            Chunk chunk = new Chunk(str, font);
            document.add(chunk);
        }

        document.close();

        File f = new File("dataOutput.pdf");
        if (!f.exists()) {
            throw new NotFoundException("File does not exist!");
        }
        /*DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());*/
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename="+f.getName());
        response.setContentLength((int) f.length());
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(f));
        System.out.println(f.delete());
        return bytes;
    }
}

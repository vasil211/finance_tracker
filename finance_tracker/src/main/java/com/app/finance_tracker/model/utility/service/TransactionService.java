package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.CreateTransactionDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.model.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class TransactionService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Transaction setFields(CreateTransactionDto transactionDto, Account account) {

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

    private boolean validateCategory(long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}

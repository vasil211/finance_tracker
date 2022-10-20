package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.entities.*;
import com.app.finance_tracker.model.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class AbstractService {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected BudgetRepository budgetRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected TransactionRepository transactionRepository;
    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    protected ModelMapper modelMapper;

    protected Budget findBudgetById(long id){
        Budget budget = budgetRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("No budget found with this id"));
        return budget;
    }

    protected Category getCategoryById(long id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("category not found"));
        return category;
    }
    protected User getUserById(long id){
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return user;
    }

    protected Account getAccountById(long id) {
        Account account =accountRepository.findById(id).orElseThrow(() -> new NotFoundException("account not found!"));
        return account;
    }

    protected Transaction findTransactionById(long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow( ()-> new NotFoundException("transaction not found!"));
        return transaction;
    }

    protected ScheduledPayment findScheduledPaymentById(long id){
        ScheduledPayment scheduledPayment = scheduledPaymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No scheduled payment found with this id"));
        return scheduledPayment;
    }
    public boolean isValidAmount(double amount){
        return amount>0;
    }

}

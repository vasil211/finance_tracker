package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;
    private BudgetValidation budgetValidation;

    @PostMapping("/create_budget")
    public Budget createBudget(@RequestBody Budget budget){

        /*if (!budgetValidation.validAmount(budget.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budget.getFromDate(),budget.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }*/



        budgetRepository.save(budget);
        return budget;
    }


}

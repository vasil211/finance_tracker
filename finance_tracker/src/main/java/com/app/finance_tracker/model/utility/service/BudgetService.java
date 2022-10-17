package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.CreateBudgetDto;
import com.app.finance_tracker.model.dto.EditBudgetDto;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BudgetService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    public Budget editFields(Budget budget, EditBudgetDto budgetDto) {

        Category wantedCategory = categoryRepository
                .findById(budgetDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("category not found"));

        budget.setAmount(budget.getAmount());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budget.setCategory(wantedCategory);

        return budget;
    }

    public Budget setFields(CreateBudgetDto budgetDto) {
        User user = userRepository.findById(budgetDto.getUserId()).get();
        Category category = categoryRepository.findById(budgetDto.getCategoryId()).get();

        if (budgetRepository.findAll().stream().anyMatch(b -> b.getCategory().getId()==category.getId() && user.getId()==b.getUser().getId())){
            throw new BadRequestException("You already have budget for that category.");
        }
        Budget budget = new Budget();
        budget.setAmount(budgetDto.getAmount());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budget.setCategory(category);
        budget.setUser(user);
        return budget;
    }
}

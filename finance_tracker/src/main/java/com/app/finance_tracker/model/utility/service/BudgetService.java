package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.budgetDTO.BudgetReturnDto;
import com.app.finance_tracker.model.dto.budgetDTO.CreateBudgetDto;
import com.app.finance_tracker.model.dto.budgetDTO.EditBudgetDto;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BudgetService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BudgetValidation budgetValidation;
    @Autowired
    private BudgetRepository budgetRepository;
    public BudgetReturnDto editFields(long id, EditBudgetDto budgetDto) {
        Budget budget = budgetRepository
                .findById(budgetDto.getId())
                .orElseThrow(() -> new NotFoundException("No budget found with this id"));
        if (!budgetValidation.validAmount(budgetDto.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(),budgetDto.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }

        Category wantedCategory = categoryRepository
                .findById(budgetDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("category not found"));

        budget.setAmount(budget.getAmount());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budget.setCategory(wantedCategory);
        budgetRepository.save(budget);
        return modelMapper.map(budget,BudgetReturnDto.class);
    }

    public BudgetReturnDto createBudget(CreateBudgetDto budgetDto) {

        if (!budgetValidation.validAmount(budgetDto.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(),budgetDto.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }

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
        budgetRepository.save(budget);
        return modelMapper.map(budget,BudgetReturnDto.class);
    }

    public List<BudgetReturnDto> getAllBudgetsForId(long userId) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("User not found!");
        }
        List<BudgetReturnDto> budgets = budgetRepository.findAll()
                .stream()
                .filter(budget -> budget.getUser().getId() == userId)
                .map( budget -> modelMapper.map(budget,BudgetReturnDto.class))
                .toList();
        return budgets;
    }
}

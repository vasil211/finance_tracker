package com.app.finance_tracker.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.Exeptionls.UnauthorizedException;
import com.app.finance_tracker.model.dto.budgetDTO.BudgetReturnDto;
import com.app.finance_tracker.model.dto.budgetDTO.CreateBudgetDto;
import com.app.finance_tracker.model.dto.budgetDTO.EditBudgetDto;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService extends AbstractService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetValidation budgetValidation;
    @Autowired
    private BudgetRepository budgetRepository;
    public BudgetReturnDto editBudget(long id, EditBudgetDto budgetDto) {
        if (!isValidAmount(budgetDto.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(),budgetDto.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }
        Budget budget = findBudgetById(id);
        if (budget.getUser().getId() != budgetDto.getUserId())
        {
            throw new UnauthorizedException("No access to this.");
        }
        Category wantedCategory = getCategoryById(budgetDto.getCategoryId());

        budget.setAmount(budget.getAmount());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budget.setCategory(wantedCategory);
        budgetRepository.save(budget);
        return modelMapper.map(budget,BudgetReturnDto.class);
    }

    public BudgetReturnDto createBudget(CreateBudgetDto budgetDto) {

        if (!isValidAmount(budgetDto.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(),budgetDto.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }

        User user = getUserById(budgetDto.getUserId());
        Category category =getCategoryById(budgetDto.getCategoryId());

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

    public BudgetReturnDto getBudgetById(long userId, long id) {
        if (!userRepository.existsById(userId))
        {
            throw new NotFoundException("User not found.");
        }
        Budget budget = findBudgetById(id);
        if (budget.getUser().getId() != userId){
            throw  new UnauthorizedException("dont have permission for this action");
        }
        return modelMapper.map(budget, BudgetReturnDto.class);
    }

    public BudgetReturnDto addMoneyToBudget(long userId, long id, double amount) {
        if (!isValidAmount(amount)){
            throw new InvalidArgumentsException("money cannot be 0 or less");
        }
        if (!userRepository.existsById(userId))
        {
            throw new NotFoundException("User not found.");
        }
        Budget budget = findBudgetById(id);
        if (budget.getUser().getId() != userId){
            throw  new UnauthorizedException("dont have permission for this action");
        }
        budget.increaseAmount(amount);
        budgetRepository.save(budget);
        return modelMapper.map(budget,BudgetReturnDto.class);
    }
}

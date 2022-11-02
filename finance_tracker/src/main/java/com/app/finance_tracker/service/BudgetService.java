package com.app.finance_tracker.service;

import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.InvalidArgumentsException;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.exceptions.UnauthorizedException;
import com.app.finance_tracker.model.dto.budgetDTO.BudgetReturnDto;
import com.app.finance_tracker.model.dto.budgetDTO.CreateBudgetDto;
import com.app.finance_tracker.model.dto.budgetDTO.EditBudgetDto;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService extends AbstractService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetValidation budgetValidation;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CurrencyExchangeService currencyExchangeService;
    public BudgetReturnDto editBudget(EditBudgetDto budgetDto, long userId) {
        Budget budget = getBudgetById(budgetDto.getId());
        if (budget.getUser().getId() != userId) {
            throw new UnauthorizedException("No access to this.");
        }
        if (!isValidAmount(budgetDto.getAmount())) {
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(), budgetDto.getToDate())) {
            throw new InvalidArgumentsException("to date cant be after from date");
        }
        if (budget.getCategory().getId() != budgetDto.getCategoryId()) {
            Category wantedCategory = getCategoryById(budgetDto.getCategoryId());
            budget.setCategory(wantedCategory);
        }
        if(budget.getCurrency().getId() != budgetDto.getCurrencyId()){
            Currency currency = getCurrencyById(budgetDto.getCurrencyId());
            double originalAmount = currencyExchangeService.getExchangedCurrency(budget.getCurrency().getCode(),
                    currency.getCode(), budget.getOriginalBudget()).getResult();
            budget.setOriginalBudget(originalAmount);
            double amount = currencyExchangeService.getExchangedCurrency(budget.getCurrency().getCode(),
                    currency.getCode(), budget.getAmount()).getResult();
            budget.setCurrency(currency);
            budget.setOriginalBudget(originalAmount);
            budget.setAmount(amount);
        }

        if (budget.getOriginalBudget() != budgetDto.getAmount()) {
            budget.setAmount(budgetDto.getAmount() - (budget.getOriginalBudget() - budget.getAmount()));
            budget.setOriginalBudget(budgetDto.getAmount());
        }

        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budgetRepository.save(budget);
        return modelMapper.map(budget, BudgetReturnDto.class);
    }

    public BudgetReturnDto createBudget(CreateBudgetDto budgetDto) {

        if (!isValidAmount(budgetDto.getAmount())) {
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(), budgetDto.getToDate())) {
            throw new InvalidArgumentsException("to date cant be after from date");
        }

        if (budgetRepository.findBudgetByCategoryIdAndUserId(budgetDto.getCategoryId(), budgetDto.getUserId()).isPresent()) {
            throw new BadRequestException("You already have budget for that category.");
        }
        User user = getUserById(budgetDto.getUserId());
        Category category = getCategoryById(budgetDto.getCategoryId());
        if (category.getUser() != null && category.getUser().getId() != user.getId()) {
            throw new NotFoundException("You dont have such category.");
        }
        Currency currency = getCurrencyById(budgetDto.getCurrencyId());

        Budget budget = new Budget();
        budget.setAmount(budgetDto.getAmount());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budget.setCategory(category);
        budget.setUser(user);
        budget.setCurrency(currency);
        budgetRepository.save(budget);
        return modelMapper.map(budget, BudgetReturnDto.class);
    }

    public List<BudgetReturnDto> getAllBudgetsForId(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        List<Budget> budgets = budgetRepository.findAllByUserId(userId);
        List<BudgetReturnDto> list = budgets.stream().map(b -> modelMapper.map(b,BudgetReturnDto.class)).toList();
        return list;
    }

    public BudgetReturnDto getBudgetById(long userId, long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        Budget budget = getBudgetById(id);
        if (budget.getUser().getId() != userId) {
            throw new UnauthorizedException("dont have permission for this action");
        }
        return modelMapper.map(budget, BudgetReturnDto.class);
    }


    public void deleteBudget(long userId, long id) {
        Budget budget = getBudgetById(id);
        if (budget.getUser().getId() != userId) {
            throw new UnauthorizedException("No permission for this action");
        }
        budgetRepository.delete(budget);
    }
    // cron job - if today is from date(day of month) -> reset amount to original amount
}

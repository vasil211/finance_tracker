package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.CreateBudgetDto;
import com.app.finance_tracker.model.dto.EditBudgetDto;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
public class BudgetController extends MasterControllerForExceptionHandlers {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetValidation budgetValidation;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create_budget")
    public ResponseEntity<Budget> createBudget(@RequestBody CreateBudgetDto budgetDto){

        if (!budgetValidation.validAmount(budgetDto.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(),budgetDto.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }
        User user = userRepository.getReferenceById(budgetDto.getUserId());
        Category category = categoryRepository.getReferenceById(budgetDto.getCategoryId());

        if (budgetRepository.findAll().stream().anyMatch(b -> b.getCategory().getId()==category.getId() && user.getId()==b.getUser().getId())){
            throw new BadRequestException("You already have budget for that category.");
        }

        Budget budget = new Budget();
        budget.setAmount(budgetDto.getAmount());
        budget.setToDate(budgetDto.getToDate());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setCategory(category);
        budget.setUser(user);
        budgetRepository.save(budget);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/get_all_budgets/{userId}")
    public ResponseEntity<List<Budget>> getAllBudgetsForUser(@PathVariable long userId){

        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("User not found!");
        }

        List<Budget> budgets = budgetRepository.findAll().stream().filter(budget -> budget.getUser().getId()== userId).collect(Collectors.toList());
        return ResponseEntity.ok(budgets);
    }

    public ResponseEntity<Budget> getBudgetById(@PathVariable long id ){
        if (budgetRepository.findById(id).isEmpty()){
            throw new NotFoundException("budget not found!");
        }
        return ResponseEntity.ok(budgetRepository.findById(id).get());
    }

    @PutMapping("/edit_budget_{id}")
    public ResponseEntity<Budget> editBudgetCategory( @RequestBody EditBudgetDto budgetDto){
        //TODO VALIDATE budgetDto
        Budget budget = budgetRepository
                .findById(budgetDto.getId())
                .orElseThrow(() -> new NotFoundException("No budget found with this id"));

        Category wantedCategory = categoryRepository
                .findById(budgetDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("category not found"));

        budget.setAmount(budget.getAmount());
        budget.setFromDate(budgetDto.getFromDate());
        budget.setToDate(budgetDto.getToDate());
        budget.setCategory(wantedCategory);
        budgetRepository.save(budget);
        return ResponseEntity.ok(budget);
    }

}

package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.BudgetReturnDto;
import com.app.finance_tracker.model.dto.CreateBudgetDto;
import com.app.finance_tracker.model.dto.EditBudgetDto;
import com.app.finance_tracker.model.entities.Budget;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.CategoryRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.service.BudgetService;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private BudgetService budgetService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create_budget")
    public ResponseEntity<BudgetReturnDto> createBudget(@RequestBody CreateBudgetDto budgetDto){

        if (!budgetValidation.validAmount(budgetDto.getAmount())){
            throw new InvalidArgumentsException("budget should be higher than 0");
        }
        if (!budgetValidation.validDate(budgetDto.getFromDate(),budgetDto.getToDate())){
            throw new InvalidArgumentsException("to date cant be after from date");
        }

        Budget budget = this.budgetService.setFields(budgetDto);
        budgetRepository.save(budget);
        return ResponseEntity.ok(modelMapper.map(budget, BudgetReturnDto.class));
    }

    @GetMapping("/get_all_budgets/{userId}")
    public ResponseEntity<List<Budget>> getAllBudgetsForUser(@PathVariable long userId){

        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("User not found!");
        }

        List<Budget> budgets = budgetRepository.findAll().stream().filter(budget -> budget.getUser().getId()== userId).collect(Collectors.toList());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{userId}/budgets/{id}")
    public ResponseEntity<BudgetReturnDto> getBudgetById(@PathVariable long userId, @PathVariable long id ){
        if (!userRepository.existsById(userId))
        {
            throw new NotFoundException("User not found.");
        }
        if (budgetRepository.findById(id).isEmpty()){
            throw new NotFoundException("budget not found!");
        }
        return ResponseEntity.ok(modelMapper.map(budgetRepository.findById(id).get(),BudgetReturnDto.class));
    }

    @PutMapping("/edit_budget_{id}")
    public ResponseEntity<Budget> editBudgetCategory( @RequestBody EditBudgetDto budgetDto){
        //TODO VALIDATE budgetDto
        Budget budget = budgetRepository
                .findById(budgetDto.getId())
                .orElseThrow(() -> new NotFoundException("No budget found with this id"));

        budget = this.budgetService.editFields(budget,budgetDto);

        budgetRepository.save(budget);
        return ResponseEntity.ok(budget);
    }

}

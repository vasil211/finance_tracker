package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.budgetDTO.BudgetReturnDto;
import com.app.finance_tracker.model.dto.budgetDTO.CreateBudgetDto;
import com.app.finance_tracker.model.dto.budgetDTO.EditBudgetDto;
import com.app.finance_tracker.model.repository.BudgetRepository;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.service.BudgetService;
import com.app.finance_tracker.model.utility.validation.BudgetValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        BudgetReturnDto budget = this.budgetService.createBudget(budgetDto);
        return ResponseEntity.ok(modelMapper.map(budget, BudgetReturnDto.class));
    }

    @GetMapping("/get_all_budgets/{userId}")
    public ResponseEntity<List<BudgetReturnDto>> getAllBudgetsForUser(@PathVariable long userId){
        List <BudgetReturnDto> list = budgetService.getAllBudgetsForId(userId);
        return ResponseEntity.ok(list);
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
    public ResponseEntity<BudgetReturnDto> editBudgetCategory( @RequestBody EditBudgetDto budgetDto,@PathVariable long id){
        BudgetReturnDto budget = this.budgetService.editFields(id,budgetDto);
        return ResponseEntity.ok(budget);
    }

}

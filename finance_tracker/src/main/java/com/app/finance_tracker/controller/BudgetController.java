package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.budgetDTO.BudgetReturnDto;
import com.app.finance_tracker.model.dto.budgetDTO.CreateBudgetDto;
import com.app.finance_tracker.model.dto.budgetDTO.EditBudgetDto;
import com.app.finance_tracker.model.utility.service.BudgetService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BudgetController extends AbstractController {

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
        BudgetReturnDto budget = budgetService.getBudgetById(userId,id);
        return ResponseEntity.ok(budget);
    }

    @PutMapping("/edit_budget/{id}")
    public ResponseEntity<BudgetReturnDto> editBudgetCategory( @RequestBody EditBudgetDto budgetDto,@PathVariable long id){
        BudgetReturnDto budget = this.budgetService.editFields(id,budgetDto);
        return ResponseEntity.ok(budget);
    }
    //ADD MONEY TO BUDGET BY ID

    //ASK KRASI HOW TO PASS AMOUNT IN REQUEST
    /*@PutMapping("/{userId}/budgets/add_money/{id}")
    public ResponseEntity<BudgetReturnDto> addMoneyToBudget(@PathVariable long userId,@PathVariable long id){
        //get userId from session when krasi explains how!
        //int userId = (int) session.getAttribute("ID");
        BudgetReturnDto result = budgetService.addMoneyToBudget(userId,id,amount);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }*/

}

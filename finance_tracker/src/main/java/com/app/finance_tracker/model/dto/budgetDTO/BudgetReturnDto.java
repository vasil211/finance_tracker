package com.app.finance_tracker.model.dto.budgetDTO;

import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import com.app.finance_tracker.model.dto.userDTO.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.Category;
import lombok.Data;

import java.util.Date;

@Data
public class BudgetReturnDto {
    private long id;
    private double amount;
    private UserWithoutPasswordDTO user;
    private CategoryForReturnDTO category;
    private Date fromDate;
    private Date toDate;
    private CurrencyForReturnDTO currency;
}

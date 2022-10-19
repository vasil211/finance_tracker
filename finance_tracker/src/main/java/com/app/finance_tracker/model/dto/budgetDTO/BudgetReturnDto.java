package com.app.finance_tracker.model.dto.budgetDTO;

import com.app.finance_tracker.model.dto.userDTO.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.Category;
import lombok.Data;

import java.util.Date;

@Data
public class BudgetReturnDto {
    private long id;
    private double amount;
    private UserWithoutPasswordDTO user;
    private Category category;
    private Date fromDate;
    private Date toDate;
}

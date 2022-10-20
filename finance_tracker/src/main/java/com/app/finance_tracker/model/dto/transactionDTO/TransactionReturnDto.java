package com.app.finance_tracker.model.dto.transactionDTO;

import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.entities.Category;
import lombok.Data;

import java.util.Date;
@Data
public class TransactionReturnDto {
    private AccountForReturnDTO account;
    private double amount;
    private CategoryForReturnDTO category;
    private Date createdAt;
    private String description;
}

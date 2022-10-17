package com.app.finance_tracker.model.dto;

import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

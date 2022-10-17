package com.app.finance_tracker.model.dto;

import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;
@Data
public class TransactionReturnDto {
    private AccountForReturnDTO account;
    private double amount;
    private Category category;
    private Date createdAt;
    private String description;
}

package com.app.finance_tracker.model.dto.transactionDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class CreateTransactionDto {
    private long accountId;
    private double amount;
    private long categoryId;
    private String description;
}

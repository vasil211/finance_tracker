package com.app.finance_tracker.model.dto.scheduledpaymentDTO;


import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.entities.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ScheduledPaymentResponseDto {
    private long id;
    private double amount;
    private LocalDate dueDate;
    private String title;
    private CategoryForReturnDTO category;
    private AccountForReturnDTO account;
}

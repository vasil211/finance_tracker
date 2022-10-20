package com.app.finance_tracker.model.dto;


import com.app.finance_tracker.model.entities.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ScheduledPaymentResponseDto {
    private long id;
    private double amount;
    private Date dueDate;
    private String title;
    private Category category;
    private AccountForReturnDTO account;
}

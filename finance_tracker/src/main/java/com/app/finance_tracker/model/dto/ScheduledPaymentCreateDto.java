package com.app.finance_tracker.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.Date;

@Data
public class ScheduledPaymentCreateDto {
    private double amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dueDate;
    private String title;
    private long categoryId;
    private long accountId;
}

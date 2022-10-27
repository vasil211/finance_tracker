package com.app.finance_tracker.model.dto.transactionDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class TransactionFilteredDto {
    private List<Long> accountIds;
    private double fromAmount;
    private double toAmount;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<Long> categoryIds;
}

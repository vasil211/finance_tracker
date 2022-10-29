package com.app.finance_tracker.model.dto.transferDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TransferFilteredDto {
    private List<Long> fromAccountsIds;
    private List<Long> toAccountsIds;
    private List<Long> ownAccountsIds;
    private double fromAmount;
    private double toAmount;
    private List<Long> currenciesIds;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String choice;
}

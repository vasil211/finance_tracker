package com.app.finance_tracker.model.dto.transferDTO;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForTransferDTO;
import com.app.finance_tracker.model.dto.userDTO.UserForTransferDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class TransferFilteredDto {
    private long id;
    private List<Integer> fromAccountsIds;
    private List<Integer> toAccountsIds;
    private double fromAmount;
    private double toAmount;
    private List<Integer> currenciesIds;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String output;
}

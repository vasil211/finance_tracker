package com.app.finance_tracker.model.dto.transferDTO;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForTransferDTO;
import com.app.finance_tracker.model.dto.userDTO.UserForTransferDTO;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TransferForReturnDTO {
    private long id;
    private double amount;
    private CurrencyForTransferDTO currency;
    private UserForTransferDTO receiver;
    private LocalDateTime date;
}

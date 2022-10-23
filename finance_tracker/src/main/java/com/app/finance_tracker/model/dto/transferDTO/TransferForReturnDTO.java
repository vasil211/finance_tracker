package com.app.finance_tracker.model.dto.transferDTO;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForTransferDTO;
import com.app.finance_tracker.model.dto.userDTO.UserForTransferDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TransferForReturnDTO {
    private long id;
    private double amount;
    private CurrencyForTransferDTO currency;
    private UserForTransferDTO receiver;
    private UserForTransferDTO sender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
}

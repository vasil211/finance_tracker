package com.app.finance_tracker.model.dto.transferDTO;

import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Currency;
import lombok.Data;

@Data
public class TransferDTO {
    private long id;
    private double amount;
    private long senderAccountId;
    private long receiverAccountId;
    private String description;
}

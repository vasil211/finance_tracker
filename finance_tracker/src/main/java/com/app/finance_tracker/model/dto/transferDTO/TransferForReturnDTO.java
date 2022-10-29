package com.app.finance_tracker.model.dto.transferDTO;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import com.app.finance_tracker.model.dto.userDTO.UserForTransferDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class TransferForReturnDTO {
    private long id;
    private double amount;
    private CurrencyForReturnDTO currency;
    private UserForTransferDTO receiver;
    private UserForTransferDTO sender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String description;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final StringBuilder sb = new StringBuilder();
        sb.append("amount : " + currency+ " ").append(amount);
        sb.append(currency.getSymbol());
        sb.append(", receiver :").append(receiver);
        sb.append(", sender : ").append(sender);
        sb.append(", date : ").append(date.format(formatter));
        if(description != null){
            sb.append(",\ndescription : ").append(description);
        }
        return sb.toString();
    }
}

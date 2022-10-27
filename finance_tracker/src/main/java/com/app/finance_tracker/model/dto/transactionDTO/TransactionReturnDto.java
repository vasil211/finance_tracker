package com.app.finance_tracker.model.dto.transactionDTO;

import com.app.finance_tracker.model.dto.accountDTO.AccountForReturnDTO;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.entities.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Data
public class TransactionReturnDto {
    private AccountForReturnDTO account;
    private double amount;
    private CategoryForReturnDTO category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String description;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("account: ").append(account)
        .append("amount: ").append(amount).append(account.getCurrency().getSymbol()).append('\n')
        .append("category: ").append(category).append('\n')
        .append("createdAt: ").append(createdAt.format(formatter)).append("\n")
        .append("description: ").append(description);
        return sb.toString();

    }
}

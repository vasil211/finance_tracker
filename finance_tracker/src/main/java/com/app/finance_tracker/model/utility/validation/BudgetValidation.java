package com.app.finance_tracker.model.utility.validation;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BudgetValidation {

    public boolean validAmount(double amount){
        return amount>0;
    }

    public boolean validDate(LocalDateTime fromDate, LocalDateTime toDate){
        return fromDate.isAfter(toDate);
    }
}

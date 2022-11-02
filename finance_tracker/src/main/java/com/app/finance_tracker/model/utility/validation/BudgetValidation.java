package com.app.finance_tracker.model.utility.validation;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class BudgetValidation {

    public boolean validDate(LocalDate fromDate, LocalDate toDate){
        return toDate.isAfter(fromDate);
    }


}

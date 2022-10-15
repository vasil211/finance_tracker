package com.app.finance_tracker.model.utility.validation;

import org.springframework.stereotype.Component;

@Component
public class AccountValidation {

    public boolean validateName(String name) {
        return name.matches("^[A-Za-z\\s]{2,20}$");
    }
}

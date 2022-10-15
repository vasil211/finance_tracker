package com.app.finance_tracker.model.utility.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }
    public boolean validateUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }
    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.])(?=\\S+$).{8,20}$");
    }
    public boolean validateFirstName(String firstName) {
        return firstName.matches("^[A-Z][a-z]{2,20}$");
    }
    public boolean validateLastName(String lastName) {
        return lastName.matches("^[A-Z][a-z]{2,20}$");
    }
}

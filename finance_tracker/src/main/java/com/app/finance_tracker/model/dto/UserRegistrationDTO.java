package com.app.finance_tracker.model.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserRegistrationDTO {

    private long id;
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String firstName;
    private String lastName;
}

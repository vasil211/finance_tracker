package com.app.finance_tracker.model.dto;

import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.springframework.context.annotation.Bean;

@Data
public class AccountForReturnDTO {
    private long id;
    private String name;
    private Currency currency;
    private double balance;
}

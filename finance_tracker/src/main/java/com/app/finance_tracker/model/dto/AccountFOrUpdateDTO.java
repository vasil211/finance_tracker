package com.app.finance_tracker.model.dto;

import lombok.Data;

@Data
public class AccountFOrUpdateDTO {
    private long id;
    private String name;
    private long currencyId;
}
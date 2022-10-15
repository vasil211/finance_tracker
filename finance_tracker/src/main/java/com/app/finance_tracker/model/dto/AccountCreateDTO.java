package com.app.finance_tracker.model.dto;

import lombok.Data;

@Data
public class AccountCreateDTO {

    private String name;
    private long currencyId;
    private long userId;
}

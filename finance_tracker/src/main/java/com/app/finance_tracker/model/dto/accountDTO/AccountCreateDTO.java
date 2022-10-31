package com.app.finance_tracker.model.dto.accountDTO;

import lombok.Data;

@Data
public class AccountCreateDTO {
    private long id;
    private String name;
    private long currencyId;
}

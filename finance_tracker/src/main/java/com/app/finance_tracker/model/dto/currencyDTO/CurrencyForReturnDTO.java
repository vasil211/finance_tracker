package com.app.finance_tracker.model.dto.currencyDTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CurrencyForReturnDTO {
    private long id;
    private String abbreviation;
    private String full_name;
}

package com.app.finance_tracker.model.dto.currencyDTO;

import com.app.finance_tracker.model.entities.Currency;
import lombok.Data;

@Data
public class CurrencyExchangeDto {
    private String success;
    private double result;
}

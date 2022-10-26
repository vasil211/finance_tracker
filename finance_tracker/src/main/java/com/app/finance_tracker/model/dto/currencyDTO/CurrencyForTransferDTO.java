package com.app.finance_tracker.model.dto.currencyDTO;

import lombok.Data;

import java.util.StringJoiner;

@Data
public class CurrencyForTransferDTO {
    private long id;
    private String code;
    private String name;
    private String symbol;
    private String namePlural;

    @Override
    public String toString() {
        return code;
    }
}

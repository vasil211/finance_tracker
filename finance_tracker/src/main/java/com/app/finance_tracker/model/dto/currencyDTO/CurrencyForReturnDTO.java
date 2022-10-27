package com.app.finance_tracker.model.dto.currencyDTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CurrencyForReturnDTO {
    private long id;
    private String code;
    private String name;
    private String symbol;
    private String namePlural;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("code: ").append(code).append('\n');
        return sb.toString();
    }
}

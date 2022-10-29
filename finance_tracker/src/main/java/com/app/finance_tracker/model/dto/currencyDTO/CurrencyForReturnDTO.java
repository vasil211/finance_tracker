package com.app.finance_tracker.model.dto.currencyDTO;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CurrencyForReturnDTO {
    private long id;
    @EqualsAndHashCode.Include
    private String code;
    private String name;
    private String symbol;
    private String namePlural;

    @Override
    public String toString() {
        return code;
    }
}

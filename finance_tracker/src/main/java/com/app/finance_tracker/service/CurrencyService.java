package com.app.finance_tracker.service;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService extends AbstractService {

    public List<CurrencyForReturnDTO> getAllCurrencies() {
        return currencyRepository.findAll().stream().map(c -> modelMapper.map(c, CurrencyForReturnDTO.class)).toList();
    }
}

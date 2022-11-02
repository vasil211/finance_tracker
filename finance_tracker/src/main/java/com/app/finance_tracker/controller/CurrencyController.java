package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
import com.app.finance_tracker.service.CurrencyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CurrencyController extends AbstractController {
    @Autowired
    private CurrencyService currencyService;


    @GetMapping("/currencies")
    public ResponseEntity<List<CurrencyForReturnDTO>> getAllCategories() {
        List<CurrencyForReturnDTO> currencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }
}

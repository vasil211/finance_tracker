package com.app.finance_tracker.service;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyExchangeDto;
import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.sun.net.httpserver.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.File;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Scanner;


@Service
public class CurrencyExchangeService  extends AbstractService{
    @Autowired
    private RestTemplate restTemplate;

    public CurrencyExchangeService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public CurrencyExchangeDto getExchangedCurrency(String from, String to, double amount) {

        StringBuilder sb = new StringBuilder();
        sb.append("https://api.apilayer.com/exchangerates_data/convert?to=");
        sb.append(to);
        sb.append("&from=").append(from).append("&amount=").append(amount);
        File file = new File("akikey.txt");

        String url = sb.toString();
        HttpHeaders headers = new HttpHeaders();
        String key = System.getenv("API_KEY");
        headers.set("apikey", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<CurrencyExchangeDto> response =
                restTemplate.exchange(url, HttpMethod.GET, requestEntity, CurrencyExchangeDto.class);
        if (response.getStatusCode()== HttpStatus.OK){
            return response.getBody();
        }
        else {
            throw new BadRequestException("Currency exchange API error : " + response.getBody().toString());
        }
    }
}

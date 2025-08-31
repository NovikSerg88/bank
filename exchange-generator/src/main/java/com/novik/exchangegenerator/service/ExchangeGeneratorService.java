package com.novik.exchangegenerator.service;

import com.novik.exchangegenerator.dto.Currency;
import com.novik.exchangegenerator.dto.RatesDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class ExchangeGeneratorService {

    @Value("${exchange.service.url}")
    private String exchangeServiceUrl;

    private final Random random = new Random();
    private final RestTemplate restTemplate;

    public ExchangeGeneratorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RatesDto> generateExchangeRates() {
        List<RatesDto> rates = new ArrayList<>();

        RatesDto rub = RatesDto.builder()
                .value(1.0)
                .currency(Currency.RUB)
                .build();

        RatesDto usd = RatesDto.builder()
                .value(75 + (90 - 75) * random.nextDouble())
                .currency(Currency.USD)
                .build();

        RatesDto cny = RatesDto.builder()
                .value(9 + (12 - 9) * random.nextDouble())
                .currency(Currency.CNY)
                .build();

        rates.add(rub);
        rates.add(usd);
        rates.add(cny);

        return rates;
    }

    @Scheduled(fixedRate = 1_000)
    public void updateExchangeRates() {
        List<RatesDto> rates = generateExchangeRates();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<RatesDto>> entity = new HttpEntity<>(rates, headers);

        restTemplate.exchange(
                exchangeServiceUrl + "/rates",
                HttpMethod.PUT,
                entity,
                void.class
        );

        log.info("Rates sent to Exchange service: {}", rates);
    }
}

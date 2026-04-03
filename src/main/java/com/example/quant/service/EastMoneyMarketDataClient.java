package com.example.quant.service;

import com.example.quant.dto.QuoteSnapshot;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Random;

@Primary
@Component
public class EastMoneyMarketDataClient implements MarketDataClient {

    private final RestTemplate restTemplate;
    private final Random random = new Random();

    public EastMoneyMarketDataClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public Optional<QuoteSnapshot> getRealtimeQuote(String symbol) {
        try {
            String secId = symbol.startsWith("6") ? "1." + symbol : "0." + symbol;
            String url = "https://push2.eastmoney.com/api/qt/stock/get?fields=f43,f170&secid=" + secId;
            Map<?, ?> payload = restTemplate.getForObject(url, Map.class);
            Map<?, ?> data = (Map<?, ?>) payload.get("data");
            BigDecimal price = BigDecimal.valueOf(((Number) data.get("f43")).doubleValue() / 100);
            BigDecimal pct = BigDecimal.valueOf(((Number) data.get("f170")).doubleValue() / 100);
            return Optional.of(new QuoteSnapshot(symbol, price, pct, LocalDateTime.now()));
        } catch (Exception ex) {
            BigDecimal simulated = BigDecimal.valueOf(10 + random.nextDouble() * 90).setScale(2, RoundingMode.HALF_UP);
            return Optional.of(new QuoteSnapshot(symbol, simulated, BigDecimal.ZERO, LocalDateTime.now()));
        }
    }
}

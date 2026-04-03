package com.example.quant.service;

import com.example.quant.dto.QuoteSnapshot;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RealtimeQuoteCacheService {

    private static final List<String> WATCHLIST = List.of("600519", "000858", "601318", "300750", "688111");

    private final MarketDataClient marketDataClient;
    private final Map<String, QuoteSnapshot> cache = new ConcurrentHashMap<>();

    public RealtimeQuoteCacheService(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    @Scheduled(fixedDelayString = "${quant.refresh-ms:5000}")
    public void refresh() {
        WATCHLIST.forEach(symbol -> marketDataClient.getRealtimeQuote(symbol).ifPresent(q -> cache.put(symbol, q)));
    }

    public Map<String, QuoteSnapshot> getCache() {
        return cache;
    }
}

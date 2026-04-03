package com.example.quant.service;

import com.example.quant.dto.QuoteSnapshot;

import java.util.Optional;

public interface MarketDataClient {
    Optional<QuoteSnapshot> getRealtimeQuote(String symbol);
}

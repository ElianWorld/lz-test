package com.example.quant.dto;

import java.math.BigDecimal;

public record BacktestResult(
        String symbol,
        int trades,
        BigDecimal winRate,
        BigDecimal annualizedReturn,
        BigDecimal maxDrawdown,
        BigDecimal sharpe
) {
}

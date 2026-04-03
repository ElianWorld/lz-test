package com.example.quant.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuoteSnapshot(
        String symbol,
        BigDecimal price,
        BigDecimal changePercent,
        LocalDateTime timestamp
) {
}

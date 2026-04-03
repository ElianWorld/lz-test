package com.example.quant.dto;

import java.math.BigDecimal;
import java.util.List;

public record RecommendationResponse(
        String symbol,
        BigDecimal currentPrice,
        boolean passed,
        BigDecimal score,
        String trendPrediction,
        BigDecimal riskPosition,
        List<String> reasons
) {
}

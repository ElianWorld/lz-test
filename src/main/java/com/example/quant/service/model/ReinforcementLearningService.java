package com.example.quant.service.model;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ReinforcementLearningService {

    public BigDecimal suggestPosition(String trend, BigDecimal volatility) {
        BigDecimal base = switch (trend) {
            case "UP" -> BigDecimal.valueOf(0.6);
            case "DOWN" -> BigDecimal.valueOf(0.1);
            default -> BigDecimal.valueOf(0.3);
        };
        BigDecimal penalty = volatility.min(BigDecimal.valueOf(0.2));
        return base.subtract(penalty).max(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
    }
}

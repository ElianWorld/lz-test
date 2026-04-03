package com.example.quant.service.model;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LstmTrendPredictorService {

    public String predictTrend(List<BigDecimal> closePrices) {
        if (closePrices.size() < 20) {
            return "NEUTRAL";
        }
        // 轻量LSTM近似：通过窗口状态门控模拟记忆与遗忘效果，用于工程骨架演示。
        BigDecimal hidden = BigDecimal.ZERO;
        for (BigDecimal price : closePrices) {
            BigDecimal gate = sigmoid(price.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            hidden = gate.multiply(price).add(BigDecimal.ONE.subtract(gate).multiply(hidden));
        }
        BigDecimal recent = closePrices.get(closePrices.size() - 1);
        if (recent.compareTo(hidden.multiply(BigDecimal.valueOf(1.02))) > 0) return "UP";
        if (recent.compareTo(hidden.multiply(BigDecimal.valueOf(0.98))) < 0) return "DOWN";
        return "NEUTRAL";
    }

    private BigDecimal sigmoid(BigDecimal x) {
        double value = 1.0 / (1.0 + Math.exp(-x.doubleValue()));
        return BigDecimal.valueOf(value);
    }
}

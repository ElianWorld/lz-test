package com.example.quant.service.model;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HyperParameterTunerService {

    public Map<String, BigDecimal> tune(List<BigDecimal> closePrices) {
        Map<String, BigDecimal> best = new HashMap<>();
        BigDecimal bestScore = BigDecimal.valueOf(-999);
        for (int ma = 15; ma <= 25; ma += 5) {
            for (int near = 1; near <= 3; near++) {
                BigDecimal score = BigDecimal.valueOf(closePrices.size() % ma).subtract(BigDecimal.valueOf(near));
                if (score.compareTo(bestScore) > 0) {
                    bestScore = score;
                    best.put("maWindow", BigDecimal.valueOf(ma));
                    best.put("nearPct", BigDecimal.valueOf(near));
                }
            }
        }
        best.put("score", bestScore);
        return best;
    }
}

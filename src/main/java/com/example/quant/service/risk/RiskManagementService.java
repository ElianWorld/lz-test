package com.example.quant.service.risk;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RiskManagementService {

    public BigDecimal applyRiskControl(BigDecimal rawPosition, BigDecimal maxPosition, BigDecimal stopLossPct) {
        BigDecimal capped = rawPosition.min(maxPosition);
        BigDecimal riskAdjusted = capped.multiply(BigDecimal.ONE.subtract(stopLossPct));
        return riskAdjusted.max(BigDecimal.valueOf(0.03)).setScale(2, RoundingMode.HALF_UP);
    }
}

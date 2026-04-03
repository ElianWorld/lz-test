package com.example.quant.service.indicator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndicatorService {

    public BigDecimal movingAverage(List<BigDecimal> prices, int period) {
        if (prices.size() < period) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> tail = prices.subList(prices.size() - period, prices.size());
        BigDecimal sum = tail.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
    }

    public List<BigDecimal> emaSeries(List<BigDecimal> prices, int period) {
        List<BigDecimal> result = new ArrayList<>();
        if (prices.isEmpty()) return result;
        BigDecimal alpha = BigDecimal.valueOf(2.0d / (period + 1));
        BigDecimal prev = prices.get(0);
        for (BigDecimal price : prices) {
            prev = alpha.multiply(price).add(BigDecimal.ONE.subtract(alpha).multiply(prev));
            result.add(prev.setScale(4, RoundingMode.HALF_UP));
        }
        return result;
    }

    public BigDecimal macdHistogram(List<BigDecimal> prices) {
        if (prices.size() < 35) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> ema12 = emaSeries(prices, 12);
        List<BigDecimal> ema26 = emaSeries(prices, 26);
        List<BigDecimal> dif = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            dif.add(ema12.get(i).subtract(ema26.get(i)));
        }
        List<BigDecimal> dea = emaSeries(dif, 9);
        return dif.get(dif.size() - 1).subtract(dea.get(dea.size() - 1)).multiply(BigDecimal.valueOf(2));
    }

    public BigDecimal rsi(List<BigDecimal> prices, int period) {
        if (prices.size() <= period) {
            return BigDecimal.valueOf(50);
        }
        BigDecimal gain = BigDecimal.ZERO;
        BigDecimal loss = BigDecimal.ZERO;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            BigDecimal diff = prices.get(i).subtract(prices.get(i - 1));
            if (diff.signum() > 0) gain = gain.add(diff);
            if (diff.signum() < 0) loss = loss.add(diff.abs());
        }
        if (loss.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.valueOf(100);
        BigDecimal rs = gain.divide(loss, 4, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 4, RoundingMode.HALF_UP));
    }

    public BigDecimal cci(List<BigDecimal> highs, List<BigDecimal> lows, List<BigDecimal> closes, int period) {
        if (closes.size() < period) return BigDecimal.ZERO;
        List<BigDecimal> tp = new ArrayList<>();
        for (int i = 0; i < closes.size(); i++) {
            tp.add(highs.get(i).add(lows.get(i)).add(closes.get(i)).divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP));
        }
        BigDecimal maTp = movingAverage(tp, period);
        BigDecimal md = BigDecimal.ZERO;
        for (int i = tp.size() - period; i < tp.size(); i++) {
            md = md.add(tp.get(i).subtract(maTp).abs());
        }
        md = md.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
        if (md.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return tp.get(tp.size() - 1).subtract(maTp).divide(md.multiply(BigDecimal.valueOf(0.015)), 4, RoundingMode.HALF_UP);
    }

    public boolean hasSimpleDivergence(List<BigDecimal> prices, List<BigDecimal> indicator) {
        if (prices.size() < 8 || indicator.size() < 8) return false;
        BigDecimal p1 = prices.get(prices.size() - 8);
        BigDecimal p2 = prices.get(prices.size() - 1);
        BigDecimal i1 = indicator.get(indicator.size() - 8);
        BigDecimal i2 = indicator.get(indicator.size() - 1);
        return p2.compareTo(p1) < 0 && i2.compareTo(i1) > 0;
    }
}

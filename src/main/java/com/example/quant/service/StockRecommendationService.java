package com.example.quant.service;

import com.example.quant.dto.BacktestResult;
import com.example.quant.dto.RecommendationResponse;
import com.example.quant.entity.StockDailyBar;
import com.example.quant.repository.StockDailyBarRepository;
import com.example.quant.service.backtest.BacktestService;
import com.example.quant.service.indicator.IndicatorService;
import com.example.quant.service.model.HyperParameterTunerService;
import com.example.quant.service.model.LstmTrendPredictorService;
import com.example.quant.service.model.ReinforcementLearningService;
import com.example.quant.service.risk.RiskManagementService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class StockRecommendationService {

    private final StockDailyBarRepository repository;
    private final RealtimeQuoteCacheService quoteCacheService;
    private final IndicatorService indicatorService;
    private final LstmTrendPredictorService lstmTrendPredictorService;
    private final HyperParameterTunerService tunerService;
    private final ReinforcementLearningService reinforcementLearningService;
    private final RiskManagementService riskManagementService;
    private final BacktestService backtestService;

    public StockRecommendationService(StockDailyBarRepository repository,
                                      RealtimeQuoteCacheService quoteCacheService,
                                      IndicatorService indicatorService,
                                      LstmTrendPredictorService lstmTrendPredictorService,
                                      HyperParameterTunerService tunerService,
                                      ReinforcementLearningService reinforcementLearningService,
                                      RiskManagementService riskManagementService,
                                      BacktestService backtestService) {
        this.repository = repository;
        this.quoteCacheService = quoteCacheService;
        this.indicatorService = indicatorService;
        this.lstmTrendPredictorService = lstmTrendPredictorService;
        this.tunerService = tunerService;
        this.reinforcementLearningService = reinforcementLearningService;
        this.riskManagementService = riskManagementService;
        this.backtestService = backtestService;
    }

    public List<RecommendationResponse> evaluate() {
        List<RecommendationResponse> results = new ArrayList<>();
        quoteCacheService.getCache().forEach((symbol, quote) -> {
            List<StockDailyBar> bars = repository.findTop120BySymbolOrderByTradingDateDesc(symbol);
            Collections.reverse(bars);
            List<BigDecimal> close = bars.stream().map(StockDailyBar::getClosePrice).toList();
            List<BigDecimal> high = bars.stream().map(StockDailyBar::getHighPrice).toList();
            List<BigDecimal> low = bars.stream().map(StockDailyBar::getLowPrice).toList();

            BigDecimal ma15 = indicatorService.movingAverage(close, 15);
            BigDecimal ma20 = indicatorService.movingAverage(close, 20);
            BigDecimal weeklyMa15 = indicatorService.movingAverage(toWeekly(close), 15);
            BigDecimal weeklyMacdHist = indicatorService.macdHistogram(toWeekly(close));
            BigDecimal dailyMacdHist = indicatorService.macdHistogram(close);
            BigDecimal cci = indicatorService.cci(high, low, close, 20);
            BigDecimal rsi = indicatorService.rsi(close, 14);
            boolean cciDivergence = cci.compareTo(BigDecimal.valueOf(-120)) > 0;
            boolean rsiDivergence = rsi.compareTo(BigDecimal.valueOf(35)) > 0;

            boolean rule1 = quote.price().compareTo(ma15) > 0 && quote.price().compareTo(weeklyMa15) > 0;
            boolean rule2 = quote.price().compareTo(ma20) > 0;
            boolean rule3 = weeklyMacdHist.compareTo(BigDecimal.ZERO) > 0;
            boolean rule4 = near(quote.price(), ma20, BigDecimal.valueOf(0.03));
            boolean rule5 = cciDivergence && rsiDivergence;
            boolean passed = rule1 && rule2 && rule3 && rule4 && rule5;

            String trend = lstmTrendPredictorService.predictTrend(close);
            BigDecimal rawPos = reinforcementLearningService.suggestPosition(trend, quote.changePercent().abs().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            BigDecimal riskPos = riskManagementService.applyRiskControl(rawPos, BigDecimal.valueOf(0.5), BigDecimal.valueOf(0.08));

            BigDecimal score = BigDecimal.ZERO
                    .add(rule1 ? BigDecimal.valueOf(20) : BigDecimal.ZERO)
                    .add(rule2 ? BigDecimal.valueOf(20) : BigDecimal.ZERO)
                    .add(rule3 ? BigDecimal.valueOf(20) : BigDecimal.ZERO)
                    .add(rule4 ? BigDecimal.valueOf(20) : BigDecimal.ZERO)
                    .add(rule5 ? BigDecimal.valueOf(20) : BigDecimal.ZERO)
                    .add(dailyMacdHist.max(BigDecimal.valueOf(-5)).min(BigDecimal.valueOf(5)));

            List<String> reasons = new ArrayList<>();
            reasons.add("日线价格高于15日均线/周线15均线: " + rule1);
            reasons.add("价格在20日均线上方: " + rule2);
            reasons.add("周线MACD红柱: " + rule3 + "，日线MACD红/绿均可，当前=" + dailyMacdHist);
            reasons.add("价格接近20日均线(±3%): " + rule4);
            reasons.add("CCI + RSI 背离信号: " + rule5);
            reasons.add("自动调参结果: " + tunerService.tune(close));

            results.add(new RecommendationResponse(symbol, quote.price(), passed, score, trend, riskPos, reasons));
        });
        results.sort(Comparator.comparing(RecommendationResponse::score).reversed());
        return results;
    }

    public List<BacktestResult> backtestTop() {
        return evaluate().stream()
                .limit(3)
                .map(r -> {
                    List<StockDailyBar> bars = repository.findTop120BySymbolOrderByTradingDateDesc(r.symbol());
                    List<BigDecimal> close = bars.stream().map(StockDailyBar::getClosePrice).toList();
                    return backtestService.run(r.symbol(), close);
                })
                .toList();
    }

    private boolean near(BigDecimal p1, BigDecimal p2, BigDecimal pct) {
        if (p2.compareTo(BigDecimal.ZERO) == 0) return false;
        BigDecimal delta = p1.subtract(p2).abs().divide(p2, 4, RoundingMode.HALF_UP);
        return delta.compareTo(pct) <= 0;
    }

    private List<BigDecimal> toWeekly(List<BigDecimal> prices) {
        List<BigDecimal> weekly = new ArrayList<>();
        for (int i = 4; i < prices.size(); i += 5) {
            weekly.add(prices.get(i));
        }
        return weekly;
    }
}

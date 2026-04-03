package com.example.quant.controller;

import com.example.quant.dto.BacktestResult;
import com.example.quant.dto.RecommendationResponse;
import com.example.quant.service.StockRecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuantController {

    private final StockRecommendationService recommendationService;

    public QuantController(StockRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }

    @GetMapping("/recommendations")
    public List<RecommendationResponse> recommendations() {
        return recommendationService.evaluate();
    }

    @GetMapping("/backtest")
    public List<BacktestResult> backtest() {
        return recommendationService.backtestTop();
    }
}

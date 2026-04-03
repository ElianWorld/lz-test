package com.example.quant.repository;

import com.example.quant.entity.StockDailyBar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockDailyBarRepository extends JpaRepository<StockDailyBar, Long> {
    List<StockDailyBar> findTop120BySymbolOrderByTradingDateDesc(String symbol);
    List<StockDailyBar> findByTradingDateBetweenOrderByTradingDateAsc(LocalDate from, LocalDate to);
}

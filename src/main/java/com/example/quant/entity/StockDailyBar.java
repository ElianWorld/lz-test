package com.example.quant.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_daily_bar", indexes = {
        @Index(name = "idx_symbol_date", columnList = "symbol,tradingDate", unique = true)
})
public class StockDailyBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String symbol;

    @Column(nullable = false)
    private LocalDate tradingDate;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal closePrice;

    @Column(nullable = false)
    private Long volume;

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public LocalDate getTradingDate() { return tradingDate; }
    public void setTradingDate(LocalDate tradingDate) { this.tradingDate = tradingDate; }
    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }
    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }
    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }
    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
}

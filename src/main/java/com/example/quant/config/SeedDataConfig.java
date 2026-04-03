package com.example.quant.config;

import com.example.quant.entity.StockDailyBar;
import com.example.quant.repository.StockDailyBarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedBars(StockDailyBarRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            List<String> symbols = List.of("600519", "000858", "601318", "300750", "688111");
            Random random = new Random(7);
            for (String symbol : symbols) {
                BigDecimal price = BigDecimal.valueOf(20 + random.nextDouble() * 80);
                for (int i = 120; i >= 1; i--) {
                    StockDailyBar bar = new StockDailyBar();
                    bar.setSymbol(symbol);
                    bar.setTradingDate(LocalDate.now().minusDays(i));
                    BigDecimal drift = BigDecimal.valueOf((random.nextDouble() - 0.45) * 0.8);
                    price = price.add(drift).max(BigDecimal.ONE);
                    bar.setOpenPrice(price);
                    bar.setHighPrice(price.add(BigDecimal.valueOf(random.nextDouble())));
                    bar.setLowPrice(price.subtract(BigDecimal.valueOf(random.nextDouble() * 0.8)).max(BigDecimal.valueOf(0.5)));
                    bar.setClosePrice(price.add(BigDecimal.valueOf((random.nextDouble() - 0.5) * 0.5)));
                    bar.setVolume((long) (100000 + random.nextInt(500000)));
                    repository.save(bar);
                }
            }
        };
    }
}

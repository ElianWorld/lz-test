package com.example.quant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuantStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantStockApplication.class, args);
    }
}

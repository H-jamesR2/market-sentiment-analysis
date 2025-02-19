package com.example.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableScheduling // <--- Required to enable scheduling!
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

package com.collector.log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LogCollectServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogCollectServerApplication.class, args);
    }
}
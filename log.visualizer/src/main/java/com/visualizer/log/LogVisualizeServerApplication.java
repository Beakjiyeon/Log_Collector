package com.visualizer.log;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LogVisualizeServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogVisualizeServerApplication.class, args);
    }
}
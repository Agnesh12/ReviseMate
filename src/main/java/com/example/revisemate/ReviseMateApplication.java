package com.example.revisemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // This annotation enables Spring's scheduling capabilities
public class ReviseMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviseMateApplication.class, args);
    }
}
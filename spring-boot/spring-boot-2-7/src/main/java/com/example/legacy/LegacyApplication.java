package com.example.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot 2.7 application class.
 * This uses patterns that will need updates for Spring Boot 3.0.
 */
@SpringBootApplication
public class LegacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegacyApplication.class, args);
    }

}

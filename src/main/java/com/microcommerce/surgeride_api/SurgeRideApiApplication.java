package com.microcommerce.surgeride_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SurgeRideApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurgeRideApiApplication.class, args);
    }

}

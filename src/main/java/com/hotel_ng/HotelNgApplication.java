package com.hotel_ng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class HotelNgApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelNgApplication.class, args);
    }
}

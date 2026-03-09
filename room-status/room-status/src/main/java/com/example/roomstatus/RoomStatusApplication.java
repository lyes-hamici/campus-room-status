package com.example.roomstatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RoomStatusApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomStatusApplication.class, args);
    }
}
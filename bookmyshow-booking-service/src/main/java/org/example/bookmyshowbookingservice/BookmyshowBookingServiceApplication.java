package org.example.bookmyshowbookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookmyshowBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmyshowBookingServiceApplication.class, args);
    }
}


package org.example.bookmyshowshowservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookmyshowShowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmyshowShowServiceApplication.class, args);
    }
}

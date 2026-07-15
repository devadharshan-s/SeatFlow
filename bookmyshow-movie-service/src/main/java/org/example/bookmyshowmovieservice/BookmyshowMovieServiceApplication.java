package org.example.bookmyshowmovieservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BookmyshowMovieServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmyshowMovieServiceApplication.class, args);
    }

}


package com.tech.snapbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class AuctionAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionAppApplication.class, args);
	}

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

}

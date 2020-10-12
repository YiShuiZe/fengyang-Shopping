package com.fengyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FyCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(FyCartApplication.class, args);
    }
}

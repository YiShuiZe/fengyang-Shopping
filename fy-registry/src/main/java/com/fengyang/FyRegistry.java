package com.fengyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class FyRegistry {
    public static void main(String[] args) {
        SpringApplication.run(FyRegistry.class, args);
    }
}

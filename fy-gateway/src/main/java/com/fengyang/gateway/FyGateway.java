package com.fengyang.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class FyGateway {
    public static void main(String[] args) {
        SpringApplication.run(FyGateway.class, args);
    }
}

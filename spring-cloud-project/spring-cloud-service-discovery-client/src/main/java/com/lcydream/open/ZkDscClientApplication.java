package com.lcydream.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //尽可能用@EnableDiscoveryClient
public class ZkDscClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZkDscClientApplication.class,args);
    }

}

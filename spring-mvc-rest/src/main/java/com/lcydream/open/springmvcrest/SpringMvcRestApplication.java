package com.lcydream.open.springmvcrest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.lcydream.open.springmvcrest.controller")
public class SpringMvcRestApplication {

    public static void main(String[] args) {
        //SpringApplication.run(SpringMvcRestApplication.class, args);
        new SpringApplicationBuilder(SpringMvcRestApplication.class)
                .run(args);
    }

}

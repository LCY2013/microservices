package com.lcydream.open.springreactive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WebFluxController {

    @GetMapping("")
    public Mono<String> index(){
        //执行计算
        printf("计算开始");
        Mono<String> mono = Mono.fromSupplier(() -> {
            printf("执行计算");
            return "hello , webFlux!";
        }); //非阻塞
        return mono;
    }

    public static void printf(Object message) {
        System.err.printf("[线程1：%s message: %s]\n",
                Thread.currentThread().getName(), message);
    }

}

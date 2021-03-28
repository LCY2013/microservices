package com.lcydream.open.springmvcrest.service;

import com.lcydream.open.springmvcrest.annotation.TransactionalService;

@TransactionalService(value = "echoServiceLuo",txName = "txName")
public class EchoService {

    public String echo(String name){
        if(1==1) {
            throw new RuntimeException("run error");
        }
        return "hello , " + name;
    }
}

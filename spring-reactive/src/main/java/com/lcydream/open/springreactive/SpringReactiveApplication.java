package com.lcydream.open.springreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
public class SpringReactiveApplication {

    public static void main(String[] args) {
        //SpringApplication.run(SpringReactiveApplication.class, args);

        GenericApplicationContext genericApplicationContext =
                new GenericApplicationContext();

        //启动监听器
        genericApplicationContext.addApplicationListener(e->
            System.err.printf("[现成：%s event:%s]\n",Thread.currentThread().getName(),e)
        );

        //启动
        genericApplicationContext.refresh();

        //push事件
        genericApplicationContext.publishEvent("magic luo");

        //关闭
        genericApplicationContext.close();
    }

}

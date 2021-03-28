package com.lcydream.open.springreactive.reactive;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class ReactorDemo {

    public static void main(String[] args) throws InterruptedException {
        //订阅模式 push模式
        Flux.just(0,1,2,3,4,5,6,7,8,9) //直接执行
                .filter(v -> v % 2 == 1)     //过滤条件，获取奇数
                .map(v -> v - 1)   //过滤的奇数全部减一  分发计算
                .reduce(Integer::sum)  //计算所有值得总和 聚合操作
//                .subscribeOn(Schedulers.elastic()) //异步执行
                .subscribeOn(Schedulers.parallel()) //异步执行
 //               .block();
                .subscribe(ReactorDemo::printf) //订阅
        ;

        Thread.sleep(2000);
    }
    public static void printf(Object message) {
        System.err.printf("[线程1：%s message: %s]\n",
                Thread.currentThread().getName(), message);
    }
}

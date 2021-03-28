package com.lcydream.open.springreactive.stream;

import java.util.stream.Stream;

public class StreamDemo {

    public static void main(String[] args) {

        //流式操作，直观，pull模式
        Stream.of(0,1,2,3,4,5,6,7,8,9,10) //模拟数据
            .filter(v -> v % 2 == 1)     //过滤条件，获取奇数
            .map(v -> v - 1)   //过滤的奇数全部减一
            .reduce(Integer::sum)  //计算所有值得总和
            .ifPresent(System.out::println) //输出计算出来的总和
            //.forEach(System.out::println) //消费属性Consumer
        ;

    }

}

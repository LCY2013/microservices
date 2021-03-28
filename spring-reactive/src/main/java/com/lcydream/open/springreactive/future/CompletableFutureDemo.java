package com.lcydream.open.springreactive.future;

import java.util.concurrent.CompletableFuture;

/**
 * @ClassName: CompletableFutureDemo
 * @author: LuoChunYun
 * @Date: 2019/4/6 15:55
 * @Description: {@link java.util.concurrent.CompletableFuture}
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {
        printf("当前线程");

        // Reactive programming
        // Fluent 流畅的
        // Streams 流式的
        // 流程编排
        // 大多数业务逻辑是数据操作
        // 消费类型  Consumer
        // 转换类型  Function
        // 提升/减少维度 map/reduce/flatMap
        CompletableFuture.supplyAsync(() -> {
            printf("hello");
            return "hello";
            //}).thenApply(result->{ //同步？
        }).thenApplyAsync(result -> { //异步？ 结果不是，说明什么问题？有因果关系，因为你有顺序的行为，所以不需要切换线程，切换会浪费资源
            printf(result + " magic");
            return result + " magic";
        }).thenAccept(CompletableFutureDemo::printf)
                .whenComplete((v, e) ->
                        printf("执行结束：" + v)
                )
                .join();

        // 三段式编程
        // 业务执行
        // 执行完成
        // 异常处理
        // 命令编程方式 Imperative programming
        try {
            // action
        } catch (Exception e) {
            // error
        } finally {
            //  complete
        }
    }

    public static void printf(String message) {
        System.err.printf("[线程1：%s message: %s]\n",
                Thread.currentThread().getName(), message);
    }

}

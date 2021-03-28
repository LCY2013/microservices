package com.lcydream.open.springreactive;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.GenericApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SpringMulticastReactiveApplication {

    public static void main(String[] args) {
        //默认是同步非阻塞
        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster =
                new SimpleApplicationEventMulticaster();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //切换成异步非阻塞
        simpleApplicationEventMulticaster.setTaskExecutor(executorService);
        //启动监听器事件
        simpleApplicationEventMulticaster.addApplicationListener(e->
            System.err.printf("[线程1：%s event:%s]\n",Thread.currentThread().getName(),e)
        );
        simpleApplicationEventMulticaster.addApplicationListener(e->
                System.err.printf("[线程2：%s event:%s]\n",Thread.currentThread().getName(),e)
        );
        simpleApplicationEventMulticaster.addApplicationListener(e->
                System.err.printf("[线程3：%s event:%s]\n",Thread.currentThread().getName(),e)
        );
        simpleApplicationEventMulticaster.addApplicationListener(e->
                System.err.printf("[线程4：%s event:%s]\n",Thread.currentThread().getName(),e)
        );

        //广播事件
        simpleApplicationEventMulticaster.multicastEvent(new PayloadApplicationEvent<String>(
                "magicLuo","magicLuo"));

        //关闭线程池
        executorService.shutdown();
    }

}

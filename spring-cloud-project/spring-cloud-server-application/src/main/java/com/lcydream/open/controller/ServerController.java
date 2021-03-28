package com.lcydream.open.controller;

import com.lcydream.open.annotation.SemaphoreCircuitBreaker;
import com.lcydream.open.annotation.SemaphoreTimeOutCircuitBreaker;
import com.lcydream.open.annotation.TimeOutCircuitBreaker;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class ServerController {

    @Value("${server.port}")
    Integer port;

    private static Random random = new Random();

    //配置Hystrix熔断信息
    @HystrixCommand(
            fallbackMethod = "responseHystrixInfo",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = "100")
            }
    )
    @RequestMapping("/say/{message}")
    public String say(@PathVariable("message")String message) throws InterruptedException {
        //随机产生一个200以内的数据
        final int hystrixValue = random.nextInt(200);
        System.out.println("port:"+port+" -> hi! "+message + ", hystrixValue:"+hystrixValue);
        //设置休眠
        TimeUnit.MILLISECONDS.sleep(hystrixValue);
        //如果小于100，就会执行下面这段内容
        System.out.println("没有被熔断：port:"+port+" -> hi! "+message);
        return "hi! "+message;
    }

    @TimeOutCircuitBreaker(timeout = 100,fallbackMethod = "responseHystrixInfo")
    @GetMapping("/say")
    public String restSay(String message) throws InterruptedException {
        //随机产生一个200以内的数据
        final int hystrixValue = random.nextInt(200);
        System.out.println("port:"+port+" -> hi! "+message + ", hystrixValue:"+hystrixValue);
        //设置休眠
        TimeUnit.MILLISECONDS.sleep(hystrixValue);
        //如果小于100，就会执行下面这段内容
        System.out.println("没有被熔断：port:"+port+" -> hi! "+message);
        return "hi! "+message;
    }

    @RequestMapping("/helloMagicTimeOut")
    @TimeOutCircuitBreaker(timeout = 100,fallbackMethod = "responseHystrixInfo")
    public String helloMagicTimeOut(String message) throws InterruptedException {
        //随机产生一个200以内的数据
        final int hystrixValue = random.nextInt(200);
        System.out.println("port:"+port+" -> hi! "+message + ", helloMagicTimeOut:"+hystrixValue);
        //设置休眠
        TimeUnit.MILLISECONDS.sleep(hystrixValue);
        //如果小于100，就会执行下面这段内容
        System.out.println("没有被熔断：port:"+port+" -> hi! "+message);
        return "hi! "+message;
    }

    @RequestMapping("/helloMagicSemaphore")
    @SemaphoreCircuitBreaker(value = 5)
    public String helloMagicSemaphore(String message) throws InterruptedException {
        //随机产生一个200以内的数据
        final int hystrixValue = random.nextInt(200);
        System.out.println("port:"+port+" -> hi! "+message + ", helloMagicSemaphore:"+hystrixValue);
        //设置休眠
        TimeUnit.MILLISECONDS.sleep(hystrixValue);
        //如果小于100，就会执行下面这段内容
        System.out.println("没有被熔断：port:"+port+" -> hi! "+message);
        return "hi! "+message;
    }

    @RequestMapping("/helloMagicSemaphoreTimeOut")
    @SemaphoreTimeOutCircuitBreaker(value = 5,timeout = 100,fallbackMethod = "com.lcydream.open.SpringCloudServerApplication#fallBackMethod")
    public String helloMagicSemaphoreTimeOut(String message) throws InterruptedException {
        //随机产生一个200以内的数据
        final int hystrixValue = random.nextInt(200);
        //System.out.println("port:"+port+" -> hi! "+message + ", helloMagicSemaphoreTimeOut:"+hystrixValue);
        //设置休眠
        TimeUnit.MILLISECONDS.sleep(hystrixValue);
        //如果小于100，就会执行下面这段内容
        //System.out.println("没有被熔断：port:"+port+" -> hi! "+message);
        return "hi! "+message;
    }

    @RequestMapping("/semaphoreTimeOut")
    @SemaphoreTimeOutCircuitBreaker(timeout = 100)
    public String semaphoreTimeOut(String message) throws InterruptedException {
        //随机产生一个200以内的数据
        final int hystrixValue = random.nextInt(200);
        //System.out.println("port:"+port+" -> hi! "+message + ", helloMagicSemaphoreTimeOut:"+hystrixValue);
        //设置休眠
        TimeUnit.MILLISECONDS.sleep(hystrixValue);
        //如果小于100，就会执行下面这段内容
        //System.out.println("没有被熔断：port:"+port+" -> hi! "+message);
        return "hi! "+message;
    }

    private String  responseHystrixInfo(String message){
        return message+",亲你的网络开小差了呀！刷新重试。fallbackMethod";
    }
}

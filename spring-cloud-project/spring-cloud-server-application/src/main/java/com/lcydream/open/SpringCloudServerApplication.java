package com.lcydream.open;

import com.lcydream.open.aop.sub.DefaultCircuitBreakerAspect;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.Semaphore;

/**
 *  @ClassName: SpringCloudClientApplication
 *  @author: LuoChunYun
 *  @Date: 2019/4/15 21:55
 *  @Description: 定义服务发现均衡调用
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableAspectJAutoProxy
public class SpringCloudServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudServerApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
        /*Semaphore semaphore = new Semaphore(5);
        semaphore.tryAcquire();
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());*/
       /* SpringCloudServerApplication springCloudServerApplication = new SpringCloudServerApplication();
        System.out.println(springCloudServerApplication.getClass().getMethods()[0].getDeclaringClass()+"."+springCloudServerApplication.getClass().getMethods()[0].getName());*/
    }

    /*@Bean
    public DefaultCircuitBreakerAspect defaultCircuitBreakerAspect() throws IllegalAccessException {
        return new DefaultCircuitBreakerAspect(20,Integer.MAX_VALUE);
    }*/

    public String fallBackMethod(String message){
        return message+",main 函数异常处理！";
    }

    public String globalFallBackMethod(){
        return "全局熔断处理！";
    }
}

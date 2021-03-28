package com.lcydream.open.annotation;

import java.lang.annotation.*;

/**
 *  @ClassName: SemaphoreCircuitBreaker 信号量限流注解
 *  @author: LuoChunYun
 *  @Date: 2019/4/20 22:11
 *  @Description: 基于信号量的熔断注解
 */
@Target(ElementType.METHOD) // 标注在方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保存注解信息
@Documented
public @interface SemaphoreCircuitBreaker {
    
    /**
     * 信号量限流注解的值，默认20的并发处理
     */ 
    int value() default 20;

    /**
     * 失败回调的方法名称
     */
    String fallbackMethod() default "";
    
}

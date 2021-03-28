package com.lcydream.open.annotation;

import java.lang.annotation.*;

/**
 *  @ClassName: SemaphoreTimeOutCircuitBreaker 基于信号量和超时时间的熔断限流
 *  @author: LuoChunYun
 *  @Date: 2019/4/20 22:19
 *  @Description: 熔断限流
 */
@Target(ElementType.METHOD) // 标注在方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保存注解信息
@Documented
public @interface SemaphoreTimeOutCircuitBreaker {

    /**
     * 信号量限流注解的值，默认20的并发处理
     */
    int value() default 20;

    /**
     * 设置超时时间，单位(ms)
     *  默认值 3000
     */
    long timeout() default 3000;

    /**
     * 失败回调的方法名称
     */
    String fallbackMethod() default "";
}

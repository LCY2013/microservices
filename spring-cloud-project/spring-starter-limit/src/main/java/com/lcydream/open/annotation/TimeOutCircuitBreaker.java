package com.lcydream.open.annotation;

import java.lang.annotation.*;

/**
 *  @ClassName: TimeOutCircuitBreaker 请求的熔断操作
 *  @author: LuoChunYun
 *  @Date: 2019/4/20 22:14
 *  @Description: 基于超时的熔断炒作
 */
@Target(ElementType.METHOD) // 标注在方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保存注解信息
@Documented
public @interface TimeOutCircuitBreaker {

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

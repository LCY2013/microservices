package com.lcydream.open.springmvcrest.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 *  @ClassName: TransactionalService
 *  @author: LuoChunYun
 *  @Date: 2019/4/5 22:05
 *  @Description: 聚合了@Service与 @Transactional的功能
 */ 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service //这个注解具有Service的功能
@Transactional //具有事务注解的功能
public @interface TransactionalService {

    //修改service注解的value变成TransactionalService的name
    @AliasFor(annotation = Service.class,attribute = "value")
    String value();  //服务名称

    @AliasFor(annotation = Transactional.class,attribute = "value")
    String txName();
}

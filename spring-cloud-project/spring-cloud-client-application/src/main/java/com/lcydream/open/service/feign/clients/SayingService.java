package com.lcydream.open.service.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * @ClassName: SayingService
 * @author: LuoChunYun
 * @Date: 2019/4/24 21:43
 * @Description: TODO
 */
@FeignClient("spring-cloud-server-application")
public interface SayingService {

    @GetMapping("/say/{message}")
    String say(@PathVariable("message") String message);

    public static void main(String[] args) throws Exception {
        Method method = SayingService.class.getMethod("say", String.class);
        Parameter parameter = method.getParameters()[0];
        System.out.println(parameter.getName());

        parameter.isNamePresent();

        DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

        Stream.of(nameDiscoverer.getParameterNames(method)).forEach(System.out::println);
    }
}

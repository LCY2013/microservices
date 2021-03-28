package com.lcydream.open;

import com.lcydream.open.annotation.EnableCustomRestClients;
import com.lcydream.open.service.feign.clients.SayingService;
import com.lcydream.open.service.rest.clients.SayingRestService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *  @ClassName: SpringCloudClientApplication
 *  @author: LuoChunYun
 *  @Date: 2019/4/15 21:55
 *  @Description: 定义服务发现均衡调用
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(clients = {SayingService.class}) //设置FeignClient
@EnableCustomRestClients(clients = {SayingRestService.class}) //设置自定义RestClient的实现类
public class SpringCloudClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudClientApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}

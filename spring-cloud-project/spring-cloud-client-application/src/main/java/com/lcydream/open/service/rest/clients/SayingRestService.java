package com.lcydream.open.service.rest.clients;

import com.lcydream.open.annotation.CustomRestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: SayingRestService
 * @author: LuoChunYun
 * @Date: 2019/4/27 10:06
 * @Description: TODO
 */
//@CustomRestClient("spring-cloud-server-application")
@CustomRestClient("${server.application.name}")
public interface SayingRestService {

    @GetMapping("/say")
    String say(@RequestParam("message") String message);

}

package com.lcydream.open.controller;

import com.lcydream.open.service.feign.clients.SayingService;
import com.lcydream.open.service.rest.clients.SayingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class ClientController {

    /*@Value("${spring.application.name}")
    private String applicationServiceName;*/

    @Autowired
    private RestTemplate restTemplate; //获取一个restTemplate的客户端

    @Autowired
    @LoadBalanced
    private RestTemplate ribbonRestTemplate; //注入RibbonRestTemplate的客户端

    @Autowired
    private SayingService sayingService; //注入FeignClient

    @Autowired
    private SayingRestService sayingRestService; //注入RestClient

    /*@Autowired
    private DiscoveryClient discoveryClient; //获取服务发现客户端

    //Map key->serviceName value->urls
    private Map<String,Set<String>> targetServiceUrlsCache = new HashMap<>();

    @Scheduled(fixedRate = 10 * 1000) //定时每隔10秒去获取刷新服务列表
    private void updateTargetUrls(){
        Map<String,Set<String>> oldServiceUrlsCache =
                new HashMap<>(this.targetServiceUrlsCache);
        Map<String,Set<String>> newServiceUrlsCache = new HashMap<>();
        discoveryClient.getServices().forEach(serviceName -> {
            //获取并刷新新的服务地址
            Set<String> newTargetUrls = discoveryClient.getInstances(serviceName)
                    .stream().map(instance ->
                            instance.isSecure() ? "https://" + instance.getHost() + ":" + instance.getPort() :
                                    "http://" + instance.getHost() + ":" + instance.getPort()
                    ).collect(Collectors.toSet());
            newServiceUrlsCache.put(serviceName,newTargetUrls);
        });
        //swap
        this.targetServiceUrlsCache = newServiceUrlsCache;
        System.out.println(System.currentTimeMillis()+":"+JSON.toJSONString(targetServiceUrlsCache));
        oldServiceUrlsCache.clear();
    }
*/
    @RequestMapping("/invoke/{serviceName}/say/{message}")
    public String invokeSay(@PathVariable String serviceName,
                            @PathVariable String message){

        //rest调用返回结果
        return restTemplate.getForObject( serviceName+"/say?message="+message,String.class,message);
    }

    @RequestMapping("/ribbon/invoke/{serviceName}/say/{message}")
    public String ribbonInvokeSay(@PathVariable String serviceName,
                            @PathVariable String message){

        //rest调用返回结果
        return ribbonRestTemplate.getForObject( "http://"+serviceName+"/say?message="+message,String.class,message);
    }

    @RequestMapping("/feign/invoke/say/{message}")
    public String feignInvokeSay(@PathVariable String message){
        //rest调用返回结果
        return sayingService.say(message);
    }

    @RequestMapping("/rest/invoke/say/{message}")
    public String restInvokeSay(@PathVariable String message){
        //rest调用返回结果
        return sayingRestService.say(message);
    }

    /*private Set<String> targetUrls = new HashSet<>(); //定义一个目标服务的地址
    @Scheduled(fixedRate = 10 * 1000) //定时每隔10秒去获取刷新服务列表
    private void updateTargetUrls(){
        Set<String> oldTargetUrls = new HashSet<>(this.targetUrls);
        //获取并刷新新的服务地址
        Set<String> newTargetUrls = discoveryClient.getInstances(applicationServiceName)
                .stream().map(instance ->
                        instance.isSecure() ? "https://" + instance.getHost() + ":" + instance.getPort() :
                                "http://" + instance.getHost() + ":" + instance.getPort()
                ).collect(Collectors.toSet());
        //swap
        this.targetUrls = newTargetUrls;
        System.out.println(System.currentTimeMillis()+":"+JSON.toJSONString(targetUrls));
        oldTargetUrls.clear();
    }
    @RequestMapping("/invoke/say/{message}")
    public String invokeSay(@PathVariable String message){
        //获取服务列表
        //负载算法
        //保证当前拿到的服务是一个快照
        final List<String> invokeTargetUrls = new ArrayList<>(this.targetUrls);
        final Random random = new Random();
        //根据随机算法获取一个索引
        final int index = random.nextInt(invokeTargetUrls.size());
        //获取某个服务
        String invokeTargetUrl = invokeTargetUrls.get(index);
        //rest调用返回结果
        return restTemplate.getForObject(invokeTargetUrl+"/say?message="+message,String.class,message);
    }*/

    /*@RequestMapping("/say")
    public String say(String message){
        System.out.println("hi! "+message);
        return "hi! "+message;
    }*/

}

package com.lcydream.open.config;

import com.lcydream.open.annotation.CustomLoadBanlance;
import com.lcydream.open.loadbalance.LoadBalancedRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;

@Configuration
public class ClientConfig {

    @Bean
    public ClientHttpRequestInterceptor clientHttpRequestInterceptor(){  //定义自定义TestTemplate拦截器
        return new LoadBalancedRequestInterceptor();
    }

    /*@Bean
    @Autowired
    public RestTemplate restTemplate(ClientHttpRequestInterceptor clientHttpRequestInterceptor){ //依赖注入RestTemplate的自定义拦截器
        RestTemplate restTemplate = new RestTemplate();
        //添加拦截器
        restTemplate.setInterceptors(Arrays.asList(clientHttpRequestInterceptor));
        return restTemplate;
    }*/

    /*@Bean
    @Qualifier
    public RestTemplate restTemplate(){ //依赖注入RestTemplate的自定义拦截器
        return new RestTemplate();
    }

    //从这里可以看出来@LoadBanlance注解是基于spring注解派生的特性，它作用的点是@Qualifier,所以我们可以自定义注解去实现这个派生
    @Bean
    @Autowired
    public Object restTemplates(@Qualifier Collection<RestTemplate> restTemplates,
                                ClientHttpRequestInterceptor clientHttpRequestInterceptor){
        restTemplates.forEach(restTemplate ->
            restTemplate.setInterceptors(Arrays.asList(clientHttpRequestInterceptor)));
        return new Object();
    }*/

    @Bean
    @CustomLoadBanlance
    public RestTemplate restTemplate(){ //依赖注入RestTemplate的自定义拦截器
        return new RestTemplate();
    }

    @Bean
    @Autowired
    public Object restTemplates(@CustomLoadBanlance Collection<RestTemplate> restTemplates,
                                ClientHttpRequestInterceptor clientHttpRequestInterceptor){
        restTemplates.forEach(restTemplate ->
                restTemplate.setInterceptors(Arrays.asList(clientHttpRequestInterceptor)));
        return new Object();
    }

    @Bean
    @LoadBalanced
    public RestTemplate loadRibbonBalanceRestTemplate(){ //注入Ribbon的RestTemplate
        return new RestTemplate();
    }

}

package com.lcydream.open.loadbalance;

import com.alibaba.fastjson.JSON;
import com.lcydream.open.http.CustomClientHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class LoadBalancedRequestInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private DiscoveryClient discoveryClient; //获取服务发现客户端

    //Map key->serviceName value->urls
    private Map<String, Set<String>> targetServiceUrlsCache = new HashMap<>();

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
        System.out.println(System.currentTimeMillis()+":"+ JSON.toJSONString(targetServiceUrlsCache));
        oldServiceUrlsCache.clear();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        //URI:/${application-name}/uri
        //URI:"/"+serviceName+"/say?message="
        final URI requestUri = request.getURI();
        final String path = requestUri.getPath();
        final String[] parts = StringUtils.delimitedListToStringArray(path.substring(1), "/");
        String serviceName = parts[0]; //serviceName
        String uri = parts[1]; //"/say?message="

        //获取服务列表
        //负载算法
        //保证当前拿到的服务是一个快照
        final List<String> invokeTargetUrls = new ArrayList<>(this.targetServiceUrlsCache.get(serviceName));
        final Random random = new Random();
        //根据随机算法获取一个索引
        final int index = random.nextInt(invokeTargetUrls.size());
        //获取某个服务
        String invokeTargetUrl = invokeTargetUrls.get(index);
        //最终服务地址
        String actualUrl = invokeTargetUrl + "/" + uri + "?" + requestUri.getQuery();

        System.out.println("本次请求的实际地址："+actualUrl);

        //执行请求
        /*final List<HttpMessageConverter<?>> messageConverters =
                Arrays.asList(
                        new ByteArrayHttpMessageConverter(),
                        new StringHttpMessageConverter());
        RestTemplate restTemplate = new RestTemplate();

        //响应内容
        final ResponseEntity<Object> forEntity = restTemplate.getForEntity(acutalUrl, Object.class);
        //获取响应头信息
        final HttpHeaders headers = forEntity.getHeaders();
        //主体内容
        //InputStream forEntityBody = forEntity.getBody().;
        //获取状态码
        HttpStatus statusCode = forEntity.getStatusCode();
        //获取响应的值
        final int statusCodeValue = forEntity.getStatusCodeValue();
        */

        //利用java去获取url
        final URL url = new URL(actualUrl);
        final URLConnection urlConnection = url.openConnection();
        //获取响应头信息
        final HttpHeaders headers = new HttpHeaders();
        //主体内容
        InputStream forEntityBody = urlConnection.getInputStream();
        //获取状态码
        HttpStatus statusCode = HttpStatus.OK;
        //获取响应的值
        final int statusCodeValue = HttpStatus.OK.value();

        return new CustomClientHttpResponse(statusCode,forEntityBody,headers,statusCodeValue);
    }

}

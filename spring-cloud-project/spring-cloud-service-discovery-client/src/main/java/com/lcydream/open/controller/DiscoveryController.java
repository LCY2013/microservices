package com.lcydream.open.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DiscoveryController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/services")
    public List<String> services(){
        return discoveryClient.getServices();
    }

    @GetMapping("/services/instances/{serviceId}")
    public List<String> getServiceInstance(@PathVariable String serviceId){
        //return discoveryClient.getInstances(serviceId);
        return discoveryClient.getInstances(serviceId)
                .stream().map(s -> s.getServiceId()+":" + s.getInstanceId() + ":"
                    + s.getHost() + ":" + s.getPort()).collect(Collectors.toList());
    }

}

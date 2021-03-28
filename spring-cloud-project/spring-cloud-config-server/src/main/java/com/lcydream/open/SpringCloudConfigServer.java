package com.lcydream.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.config.ConfigServerConfiguration;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigServer {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServer.class,args);
    }

    /**
     *  @ClassName: SpringCloudConfigServer
     *  @author: LuoChunYun
     *  @Date: 2019/4/12 21:47
     *  @Description: 创建一个自定义的环境仓储
     */
    @Bean
    public EnvironmentRepository environmentRepository(){
        return (String application, String profile, String label) -> {
            Environment environment = new Environment("config-client",profile);
            List<PropertySource> propertySources = environment.getPropertySources();
            Map<String,Object> mapPropertySource = new HashMap<>();
            mapPropertySource.put("name","luo");
            mapPropertySource.put("age","18");
            mapPropertySource.put("addr","成都");
            PropertySource propertySource = new PropertySource("map",mapPropertySource);
            propertySources.add(propertySource);
            return environment;
        };
    }

}

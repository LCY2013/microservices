package com.lcydream.open.locator;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 *  @ClassName: CustomConfigServiceBootstrapConfiguration
 *  @author: LuoChunYun
 *  @Date: 2019/4/12 22:00
 *  @Description: 自定义bootstrapConfiguration
 */ 
@Configuration
public class CustomConfigServiceBootstrapConfiguration {

    @Bean
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
        ConfigClientProperties clientProperties = configClientProperties();
        ConfigServicePropertySourceLocator configServicePropertySourceLocator =  new ConfigServicePropertySourceLocator(clientProperties);
        configServicePropertySourceLocator.setRestTemplate(customRestTemplate(clientProperties));
        return configServicePropertySourceLocator;
    }

    private ConfigClientProperties configClientProperties() {
        Environment environment = new StandardServletEnvironment();
        ConfigClientProperties configClientProperties = new ConfigClientProperties(environment);
        configClientProperties.setUri(new String[]{"http://127.0.0.1:1995"});
        return configClientProperties;
    }

    private RestTemplate customRestTemplate(ConfigClientProperties clientProperties) {
        return new RestTemplate();
    }
}

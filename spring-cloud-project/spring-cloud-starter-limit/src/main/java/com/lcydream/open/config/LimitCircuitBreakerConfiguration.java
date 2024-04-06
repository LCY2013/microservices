package com.lcydream.open.config;

import com.lcydream.open.aop.CircuitBreakerAspect;
import com.lcydream.open.aop.sub.DefaultCircuitBreakerAspect;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.actuator.NamedFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: LimitCircuitBreakerConfiguration
 * @author: LuoChunYun
 * @Date: 2019/4/21 18:00
 * @Description: TODO
 */
@Configuration
@EnableConfigurationProperties({ LimitProperties.class })
public class LimitCircuitBreakerConfiguration {

    private final LimitProperties properties;

    @Autowired
    public LimitCircuitBreakerConfiguration(LimitProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DefaultCircuitBreakerAspect defaultCircuitBreakerAspect() throws IllegalAccessException {
        return new DefaultCircuitBreakerAspect(properties.getCoreExecutorCount(),properties.getMaxExecutorCount(),properties.getFallbackMethod());
    }

    @Bean
    public LimitShutdownHook limitShutdownHook() {
        return new LimitShutdownHook();
    }

    @Bean
    public HasFeatures limitFeature() {
        return HasFeatures
                .namedFeatures(new NamedFeature("limitLuo", DefaultCircuitBreakerAspect.class));
    }

    /**
     * {@link DisposableBean} that makes sure that Hystrix internal state is cleared when
     * {@link ApplicationContext} shuts down.
     */
    private class LimitShutdownHook implements DisposableBean {

        @Override
        public void destroy() throws Exception {
            // Just call Hystrix to reset thread pool etc.
            CircuitBreakerAspect.shutDown();
        }

    }
}

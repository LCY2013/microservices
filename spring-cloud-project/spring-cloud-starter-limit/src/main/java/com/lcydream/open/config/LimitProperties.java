package com.lcydream.open.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: LimitAutoConfiguration
 * @author: LuoChunYun
 * @Date: 2019/4/21 18:02
 * @Description: TODO
 */
@ConfigurationProperties(prefix = "limit.manager")
public class LimitProperties {

    private int coreExecutorCount = 20;

    private int maxExecutorCount = Integer.MAX_VALUE;

    private String fallbackMethod = "";

    public int getCoreExecutorCount() {
        return coreExecutorCount;
    }

    public void setCoreExecutorCount(int coreExecutorCount) {
        this.coreExecutorCount = coreExecutorCount;
    }

    public int getMaxExecutorCount() {
        return maxExecutorCount;
    }

    public void setMaxExecutorCount(int maxExecutorCount) {
        this.maxExecutorCount = maxExecutorCount;
    }

    public String getFallbackMethod() {
        return fallbackMethod;
    }

    public void setFallbackMethod(String fallbackMethod) {
        this.fallbackMethod = fallbackMethod;
    }
}

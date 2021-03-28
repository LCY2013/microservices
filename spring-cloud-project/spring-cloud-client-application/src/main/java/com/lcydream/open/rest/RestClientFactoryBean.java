package com.lcydream.open.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName: RestClientFactoryBean
 * @author: LuoChunYun
 * @Date: 2019/4/27 15:10
 * @Description: TODO
 */
public class RestClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    /***********************************
     * WARNING! Nothing in this class should be @Autowired. It causes NPEs because of some
     * lifecycle race condition.
     ***********************************/

    private Class<?> type;

    private String name;

    private String url;

    private String contextId;

    private String path;

    private boolean decode404;

    private ApplicationContext applicationContext;

    private Class<?> fallback = void.class;

    private Class<?> fallbackFactory = void.class;

    @Nullable
    @Override
    public Object getObject() throws Exception {
        return getTarget();
    }

    /**
     * @param <T> the target type of the Feign client
     * @return a {@link RestClientSpecification} client created with the specified data and the context
     * information
     */
    <T> T getTarget() {
        if (!StringUtils.hasText(this.url)) {
            if (!this.name.startsWith("http")) {
                this.url = "http://" + this.name;
            }
            else {
                this.url = this.name;
            }
        }
        if (StringUtils.hasText(this.url) && !this.url.startsWith("http")) {
            this.url = "http://" + this.url;
        }
        try {
            final RestTemplate restTemplate = applicationContext.getBean("loadRibbonBalanceRestTemplate",RestTemplate.class);

            return (T)new ReflectiveRest(type,restTemplate,url).newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.contextId, "Context id must be set");
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDecode404() {
        return decode404;
    }

    public void setDecode404(boolean decode404) {
        this.decode404 = decode404;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Class<?> getFallback() {
        return fallback;
    }

    public void setFallback(Class<?> fallback) {
        this.fallback = fallback;
    }

    public Class<?> getFallbackFactory() {
        return fallbackFactory;
    }

    public void setFallbackFactory(Class<?> fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
    }
}

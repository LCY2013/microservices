package com.lcydream.open.rest;

import org.springframework.cloud.context.named.NamedContextFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * @ClassName: RestClientSpecification
 * @author: LuoChunYun
 * @Date: 2019/4/27 15:01
 * @Description: TODO
 */
public class RestClientSpecification implements NamedContextFactory.Specification {

    private String name;

    private Class<?>[] configuration;

    RestClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return new Class[0];
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestClientSpecification that = (RestClientSpecification) o;
        return Objects.equals(this.name, that.name)
                && Arrays.equals(this.configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.configuration);
    }

    @Override
    public String toString() {
        return new StringBuilder("RestClientSpecification{").append("name='")
                .append(this.name).append("', ").append("configuration=")
                .append(Arrays.toString(this.configuration)).append("}").toString();
    }
}

package com.lcydream.open.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import static java.lang.String.format;

/**
 * @ClassName: ReflectiveRest
 * @author: LuoChunYun
 * @Date: 2019/4/27 15:31
 * @Description: TODO
 */
public class ReflectiveRest {

    private final Class<?> restClientClass;

    private final RestTemplate restTemplate;

    private final String url;

    private static Logger logger = LoggerFactory.getLogger(ReflectiveRest.class);

    public ReflectiveRest(Class<?> restClientClass,
                          RestTemplate restTemplate, String url) {
        this.restClientClass = restClientClass;
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public <T> T newInstance() {
        return (T)Proxy.newProxyInstance(restClientClass.getClassLoader(),new Class[]{restClientClass},
                new RestInvocationHandler(restTemplate,url));
    }

    static class RestInvocationHandler implements InvocationHandler {

        private final RestTemplate restTemplate;

        private final String url;

        RestInvocationHandler(RestTemplate restTemplate,String url) {
            this.restTemplate = restTemplate;
            this.url = url;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            //this.url = http://serviceName

            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler =
                            args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else if ("hashCode".equals(method.getName())) {
                return hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }

            //过滤掉不存在合法注解的方法执行
            final Annotation[] annotationsMethod = method.getAnnotations();
            final boolean anyMatchRequest = Arrays.stream(annotationsMethod)
                    .map(Annotation::annotationType)
                    .peek(System.out::println)
                    .anyMatch(this::isRequestMapping);
            if(anyMatchRequest) {
                // 过滤 @RequestMapping 方法
                GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
                PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
                DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
                RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                //得到方法的URI
                String[] uri = null;
                if(getMapping != null){
                    uri = getMapping.value();
                }
                if(postMapping != null && uri==null){
                    uri = postMapping.value();
                }
                if(deleteMapping != null && uri==null){
                    uri = deleteMapping.value();
                }
                if(requestMapping != null && uri==null){
                    uri = requestMapping.value();
                }
                if (uri != null) {
                    for(int index=0;index<uri.length;index++) {
                        // http://${serviceName}/${uri}
                        StringBuilder urlBuilder = new StringBuilder(url).append("/").append(uri[index]);
                        // 获取方法参数数量
                        int count = method.getParameterCount();
                        // 方法参数是有顺序
                        // FIXME
                        //String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
                        // 方法参数类型集合
                        Class<?>[] paramTypes = method.getParameterTypes();
                        Annotation[][] annotations = method.getParameterAnnotations();
                        StringBuilder queryStringBuilder = new StringBuilder();
                        for (int i = 0; i < count; i++) {
                            Annotation[] paramAnnotations = annotations[i];
                            Class<?> paramType = paramTypes[i];
                            RequestParam requestParam = (RequestParam) paramAnnotations[0];
                            if (requestParam != null) {
                                String paramName = "";
//                            paramNames[i];
                                // HTTP 请求参数
                                String requestParamName = StringUtils.hasText(requestParam.value()) ? requestParam.value() :
                                        paramName;
                                String requestParamValue = String.class.equals(paramType)
                                        ? (String) args[i] : String.valueOf(args[i]);
                                // uri?name=value&n2=v2&n3=v3
                                queryStringBuilder.append("&")
                                        .append(requestParamName).append("=").append(requestParamValue);
                            }
                        }

                        String queryString = queryStringBuilder.toString();
                        if (StringUtils.hasText(queryString)) {
                            urlBuilder.append("?").append(queryString);
                        }

                        // http://${serviceName}/${uri}?${queryString}
                        String url = urlBuilder.toString();
                        try {
                            return restTemplate.getForObject(url, method.getReturnType());
                        }catch (Exception e){
                            logger.error(this.url+"/"+uri[index]+"  ,The visit was unsuccessful");
                        }
                    }
                }
            }
            return null;
        }

        public static <T> T checkNotNull(T reference,
                                         String errorMessageTemplate,
                                         Object... errorMessageArgs) {
            if (reference == null) {
                // If either of these parameters is null, the right thing happens anyway
                throw new NullPointerException(
                        format(errorMessageTemplate, errorMessageArgs));
            }
            return reference;
        }

        public boolean isRequestMapping(Class clazz){
            return Arrays.stream(new Class[]{GetMapping.class,
                    PostMapping.class, RequestMapping.class, DeleteMapping.class,
                    PutMapping.class, PatchMapping.class,}).anyMatch(matchClazz -> clazz.equals(matchClazz));
        }

    }


}

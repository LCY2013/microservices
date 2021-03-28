package com.lcydream.open.springcloudnativeapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringCloudNativeApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setId("magic luo context");
        //在'magic luo context'这个上下文中注册一个名称叫helloMagic的bean
        applicationContext.registerBean("helloMagic",
                String.class,
                "hello magic");
        //启动'magic luo context'上下文
        applicationContext.refresh();

        //类比与Spring WebMVC,Root WebApplication和DispatcherServlet WebApplication
        //DispatcherServlet WebApplication parent = Root WebApplication
        //DispatcherServlet Servlet
        //Filter -> Root WebApplication

        new SpringApplicationBuilder(SpringCloudNativeApplication.class)
                .parent(applicationContext) //显示设置双亲上下文
                .run(args);
        //SpringApplication.run(SpringCloudNativeApplication.class, args);
    }

    @Autowired
    @Qualifier(value = "helloMagic")
    private String message; //string bean

    @RequestMapping("")
    public String index(){
        return message;
    }
}

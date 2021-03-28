package com.lcydream.open.springmvcrest;

import com.lcydream.open.springmvcrest.service.EchoService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionStatus;

@ComponentScan(basePackages = "com.lcydream.open.springmvcrest.service")
@EnableTransactionManagement
public class SpringAnnotationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext();
        //注册SpringApplication扫描com.lcydream.open.springmvcrest.service
        annotationConfigApplicationContext.register(SpringAnnotationApplication.class);

        //启动
        annotationConfigApplicationContext.refresh();

        //获取echoService的bean对象
        annotationConfigApplicationContext.getBeansOfType(EchoService.class)
            .forEach((beanName,beanType)-> {
                System.err.println("beanName:" + beanName + "->" + beanType);
                System.err.println(beanType.echo("magic"));
            });

        //关闭上下文
        annotationConfigApplicationContext.close();
    }

    @Bean(name = "txName")
    public PlatformTransactionManager platformTransactionManager(){
        return new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
                return new DefaultTransactionStatus(definition, true, true,
                        definition.isReadOnly(), true, true);
            }

            @Override
            public void commit(TransactionStatus status) throws TransactionException {
                System.err.println("commit...");
            }

            @Override
            public void rollback(TransactionStatus status) throws TransactionException {
                System.err.println("rollback...");
            }
        };
    }
}

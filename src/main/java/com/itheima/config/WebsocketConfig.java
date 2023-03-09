package com.itheima.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

//@Configuration是一个类级别的注释，表明一个对象是 bean 定义的来源。@Configuration类通过带@Bean注释的方法声明 bean 。@Bean对@Configuration类方法的调用也可用于定义 bean 间的依赖关系
//@Configuration定义一个配置类并交给spring管理的时候你就可以在相关类上面加这个注解，并配合@Bean注解把对象交个spring去管理
@Configuration
public class WebsocketConfig {
    /**
     * 	注入ServerEndpointExporter，
     * 	这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     */
    //将方法的返回值交给spring管理
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}

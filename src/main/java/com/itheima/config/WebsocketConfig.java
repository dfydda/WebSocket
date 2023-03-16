package com.itheima.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

//@Configuration是一个类级别的注释，表明一个对象是 bean 定义的来源。@Configuration类通过带@Bean注释的方法声明 bean 。@Bean对@Configuration类方法的调用也可用于定义 bean 间的依赖关系
//@Configuration定义一个配置类并交给spring管理的时候你就可以在相关类上面加这个注解，并配合@Bean注解把对象交个spring去管理
@Configuration
@Component
public class WebsocketConfig extends ServerEndpointConfig.Configurator {
    /**
     * 	注入ServerEndpointExporter，
     * 	这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     */
    //将方法的返回值交给spring管理
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //获取httpSession对象
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        //将HttpSession对象保存起来
        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}

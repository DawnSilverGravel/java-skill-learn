package com.silvergravel.spring.config;


import com.silvergravel.spring.handler.ChatWebsocketHandler;
import com.silvergravel.spring.interceptor.ChatHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/28
 */
@Configuration
public class SpringWebsocketConfiguration implements WebSocketConfigurer {
    private final String websocketPrefix = "/spring";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), websocketPrefix + "/**")
                .addInterceptors(handshakeInterceptor())
                // 新版本
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new ChatHandshakeInterceptor(websocketPrefix);
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new ChatWebsocketHandler(websocketPrefix);
    }


    /**
     * 自定义服务器属性容器配置
     *
     * @return 自定义服务器容器属性配置
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}

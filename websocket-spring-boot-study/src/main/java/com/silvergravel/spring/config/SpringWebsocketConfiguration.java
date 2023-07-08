package com.silvergravel.spring.config;

import com.silvergravel.spring.handler.MyWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/28
 */
@Configuration
public class SpringWebsocketConfiguration implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/spring/**")
                .addInterceptors(handshakeInterceptor())
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new MyHandshakeInterceptor();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new MyWebsocketHandler();
    }
}

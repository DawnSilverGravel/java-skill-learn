package com.silvergravel.stomp.config;

import com.silvergravel.stomp.interceptor.ChatInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/29
 */
@EnableWebSocketMessageBroker
@Configuration
public class StompWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").setAllowedOriginPatterns("*").addInterceptors();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/chat");
        config.enableSimpleBroker("/topic","/sliver-gravel");
        config.setPreservePublishOrder(true);
        // 用户主题的前缀：默认是/user
        config.setUserDestinationPrefix("/sliver-gravel");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChatInterceptor());
    }


}

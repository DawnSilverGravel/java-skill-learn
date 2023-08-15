
package com.silvergravel.stomp.config;


import com.silvergravel.stomp.interceptor.ChatInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
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
        config.enableSimpleBroker("/topic", "/queue","/sliver-gravel");
        // 用户主题的前缀：默认是user
        config.setUserDestinationPrefix("/sliver-gravel");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatInterceptor());
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
//                StompHeaderAccessor accessor =
//                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                assert accessor != null;
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String sessionId = accessor.getSessionId();
//                    List<String> usernames = accessor.getNativeHeader("username");
//                    if (sessionId != null && usernames != null && usernames.size() > 0) {
//                        String username = usernames.get(0);
//                        String currentSessionId = STOMP_USERNAME_CHANNEL_MAP.put(username, sessionId);
//                        if (currentSessionId != null) {
//                            System.out.println(sessionId);
//                        }
//                    }
//
//                }
//
//                System.out.println(message);
//                ChannelInterceptor.super.afterReceiveCompletion(message, channel, ex);
//            }
//        });
//        WebSocketMessageBrokerConfigurer.super.configureClientOutboundChannel(registration);
//    }

    public ChannelInterceptor chatInterceptor() {
        return new ChatInterceptor();
    }


}

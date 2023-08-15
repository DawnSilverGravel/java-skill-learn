
package com.silvergravel.stomp.interceptor;

import com.silvergravel.stomp.service.StompChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/8/15
 */
public class ChatInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }
        System.out.println(Arrays.toString(accessor.getHeartbeat()));
        System.out.println(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String username = accessor.getLogin();
            if (sessionId != null && username != null) {
                String lastSessionId = StompChatService.addUser(username, sessionId);
                // 如果之前有登陆过的账号，则将之前的账号关闭
                if (lastSessionId != null) {
                    Map<String, Object> headers = new HashMap<>(5);
                    headers.put("simpMessageType", SimpMessageType.DISCONNECT);
                    headers.put("stompCommand", StompCommand.DISCONNECT);
                    headers.put("simpSessionAttributes", new HashMap<>(0));
                    headers.put("simpSessionId", lastSessionId);
                    GenericMessage<byte[]> genericMessage = new GenericMessage<>(new byte[0], headers);
                    System.err.println("new: "+genericMessage);
                    channel.send(genericMessage);
                }
            }
        }
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // 生成 Message类型信息，路由到 用户在线列表
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return;
        }
        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return;
        }
        String username = Objects.requireNonNull(accessor.getLogin());
        Map<String, Object> headers = genericHeaders(accessor.getSessionId(), SimpMessageType.MESSAGE, StompCommand.SEND);
        Map<String, Object> nativeHeaders = new HashMap<>(2);
        nativeHeaders.put("destination", Collections.singletonList("/chat/user-list"));
        nativeHeaders.put("content-length", Collections.singletonList(username.length()));
        headers.put("nativeHeaders", nativeHeaders);
        headers.put("simpDestination","/chat/user-list");
        GenericMessage<byte[]> genericMessage = new GenericMessage<>(username.getBytes(StandardCharsets.UTF_8), headers);
        channel.send(genericMessage);
    }


    private Map<String, Object> genericHeaders(String sessionId, SimpMessageType simpMessageType, StompCommand command) {
        Map<String, Object> headers = new HashMap<>(5);
        headers.put("simpMessageType", simpMessageType);
        headers.put("stompCommand", command);
        headers.put("simpSessionAttributes", new HashMap<>(0));
        headers.put("simpSessionId", sessionId);
        return headers;
    }

//    private static class MessageArgs{
//        private final static Map<String,String> DEFAULT_SIMP_SESSION_ATTRIBUTES = new HashMap<>(0);
//        private final static Long[] DEFAULT_SIMP_HEARTBEAT= new Long[]{0L,0L};
//        private final String simpMessageType = "simpMessageType";
//        private final String stompCommand = "stompCommand";
//        private final String simpSessionAttributes = "simpSessionAttributes";
//        private final String simpHeartbeat = "simpHeartbeat";
//        private final String simpSessionId = "simpSessionId";
//        private final String }

}



package com.silvergravel.stomp.interceptor;


import com.silvergravel.stomp.service.StompChatService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/8/15
 */
public class ChatInterceptor implements ChannelInterceptor {

    private final MessageService messageService = new MessageService();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String username = accessor.getLogin();
            if (sessionId != null && username != null) {
                // 强制下线
                messageService.forcedOffline(sessionId, username, channel);
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
        Object simpHeart = message.getHeaders().get(StompHeaderAccessor.HEART_BEAT_HEADER);
        List<String> heart = accessor.getNativeHeader(StompHeaderAccessor.STOMP_HEARTBEAT_HEADER);
        // 客户端关闭连接会 接收到两条 DISCONNECT 消息， 有一个有心跳键值，一个没有心跳键值，故选择一个
        // 学识尚浅，不知原因。
        boolean disconnect = StompCommand.DISCONNECT.equals(accessor.getCommand()) && (heart != null || simpHeart != null);
        if (disconnect) {
            messageService.sendOffline(accessor.getSessionId(), accessor.getLogin(), channel);
            return;
        }
        // 发送上线信息的时候，自己也可能会接收到，所以前端做个兼容处理
        // 接收到自己的消息就不做处理
        // 原因可能是异步的原因
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String username = accessor.getLogin();
            if (sessionId != null && username != null) {
                messageService.sendOnline(sessionId, username, channel);
            }
            return;
        }
        // Connect消息发送完毕之后，保证订阅该主题之后再发送，发送用户上线信息
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getLogin() != null) {
            messageService.sendUserList(accessor.getSessionId(), accessor.getLogin(), channel);
        }
    }


    private StompHeaderAccessor stompHeaderAccessor(StompCommand command, SimpMessageType messageType) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(command);
        stompHeaderAccessor.setMessageTypeIfNotSet(messageType);
        stompHeaderAccessor.setSessionAttributes(new HashMap<>(0));
        return stompHeaderAccessor;
    }

    /**
     * 消息服务
     */
    private class MessageService {

        /**
         * 强制下线
         *
         * @param sessionId 当前进行连接的sessionID
         * @param username  对应的用户
         * @param channel   管道
         */
        void forcedOffline(String sessionId, String username, MessageChannel channel) {
            // 如果之前有登陆过的账号，则将之前的账号关闭
            String lastSessionId = StompChatService.addUser(username, sessionId);
            if (lastSessionId != null) {
                // 发送错误的信息
                sendErrorMessage(lastSessionId, username, channel);
                // 暂停一会，保证错误信息在关闭之前发送
                try {
                    TimeUnit.MILLISECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 该lastSessionId的连接关闭
                StompHeaderAccessor accessor = stompHeaderAccessor(StompCommand.DISCONNECT, SimpMessageType.DISCONNECT);
                accessor.setSessionId(lastSessionId);
                // 设置心跳用于关闭连接信息，因为有两条关闭信息，所以用作区分
                accessor.setHeartbeat(0, 0);
                GenericMessage<byte[]> genericMessage = new GenericMessage<>(new byte[0], accessor.getMessageHeaders());
                channel.send(genericMessage);
            }
        }

        void sendOffline(String sessionId, String username, MessageChannel channel) {
            if (username == null) {
                username = StompChatService.removeUser(sessionId);
                if (username == null) {
                    System.err.println("sessionId：" + sessionId + " 不存在");
                    return;
                }
            }
            // 发送下线信息
            StompHeaderAccessor accessor = stompHeaderAccessor(StompCommand.SEND, SimpMessageType.MESSAGE);
            accessor.setNativeHeaderValues("destination", Collections.singletonList("/chat/offline"));
            accessor.setNativeHeaderValues("content-length", Collections.singletonList(String.valueOf(username.length())));
            accessor.setDestination("/chat/offline");
            accessor.setHeartbeat(1000, 1000);
            accessor.setSessionId(sessionId);
            GenericMessage<byte[]> genericMessage = new GenericMessage<>(username.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
            channel.send(genericMessage);
        }

        void sendOnline(String sessionId, String username, MessageChannel channel) {
            StompHeaderAccessor accessor = stompHeaderAccessor(StompCommand.SEND, SimpMessageType.MESSAGE);
            accessor.setNativeHeaderValues("destination", Collections.singletonList("/chat/online"));
            accessor.setNativeHeaderValues("content-length", Collections.singletonList(String.valueOf(username.length())));
            accessor.setDestination("/chat/online");
            accessor.setHeartbeat(1000, 1000);
            accessor.setSessionId(sessionId);
            GenericMessage<byte[]> genericMessage = new GenericMessage<>(username.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
            channel.send(genericMessage);
        }

        void sendUserList(String sessionId, String username, MessageChannel channel) {

            StompHeaderAccessor accessor = stompHeaderAccessor(StompCommand.SEND, SimpMessageType.MESSAGE);
            accessor.setNativeHeaderValues("destination", Collections.singletonList("/chat/user-list"));
            accessor.setNativeHeaderValues("content-length", Collections.singletonList(String.valueOf(username.length())));
            accessor.setDestination("/chat/user-list");
            accessor.setHeartbeat(1000, 1000);
            accessor.setSessionId(sessionId);
            GenericMessage<byte[]> genericMessage = new GenericMessage<>(username.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
            channel.send(genericMessage);
        }

        void sendErrorMessage(String sessionId, String username, MessageChannel channel) {
            StompHeaderAccessor accessor = stompHeaderAccessor(StompCommand.SEND, SimpMessageType.MESSAGE);
            accessor.setNativeHeaderValues("destination", Collections.singletonList("/chat/forced-offline"));
            accessor.setNativeHeaderValues("content-length", Collections.singletonList(String.valueOf(username.length())));
            accessor.setDestination("/chat/forced-offline");
            accessor.setHeartbeat(1000, 1000);
            accessor.setSessionId(sessionId);
            GenericMessage<byte[]> genericMessage = new GenericMessage<>(username.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
            channel.send(genericMessage);

        }

//        private void sendCommand(String sessionId, String command, MessageChannel channel) {
//            StompHeaderAccessor accessor = stompHeaderAccessor(StompCommand.SEND, SimpMessageType.MESSAGE);
//            accessor.setNativeHeaderValues("destination", Collections.singletonList("/chat/event-handle"));
//            accessor.setNativeHeaderValues("content-length", Collections.singletonList(String.valueOf(command.length())));
//            accessor.setDestination("/chat/event-handle");
//            accessor.setHeartbeat(1000, 1000);
//            accessor.setSessionId(sessionId);
//            GenericMessage<byte[]> genericMessage = new GenericMessage<>(command.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
//            channel.send(genericMessage);
//        }

    }

}

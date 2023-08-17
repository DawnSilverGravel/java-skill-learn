package com.silvergravel.spring.handler;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNullApi;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/28
 */
@Slf4j
public class ChatWebsocketHandler implements WebSocketHandler {


    private final static ConcurrentHashMap<String, WebSocketSession> USERNAME_SESSION_MAP = new ConcurrentHashMap<>();

    private final String websocketPrefix;

    private final ObjectMapper mapper = new ObjectMapper();

    public ChatWebsocketHandler(String websocketPrefix) {
        this.websocketPrefix = websocketPrefix;
    }


    /**
     * 建立完成之后要做的事情
     * @param session 当前session
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 建立连接之后需要完成的事情
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.getPath();
            String username = path.replace(websocketPrefix + "/", "");
            WebSocketSession webSocketSession = USERNAME_SESSION_MAP.remove(username);
            // 先发送挤下线通知
            if (webSocketSession != null) {
                ChatProtocol<ChatProtocol.ErrorMessage> chatProtocol = MessageUtil.errorMessageChatProtocol("-1", "账号在别处登录 您被已被挤下线!");
                WebSocketMessage<String> message = new TextMessage(mapper.writeValueAsString(chatProtocol));
                webSocketSession.sendMessage(message);
                webSocketSession.close();
                log.warn("spring：用户 {} 被挤下线",username);
            }
            // 对所有用户发送上线通知
            log.info("spring：用户 {} 上线",username);
            sendOnlineState(USERNAME_SESSION_MAP.values(), username, true);
            pushUserList(session, USERNAME_SESSION_MAP.keys());
            USERNAME_SESSION_MAP.put(username, session);
            return;
        }
        throw new RuntimeException("没有用户数据");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Object payload = message.getPayload();
        System.out.println(payload);
        // ChatProtocol<ChatProtocol.Message> chatProtocol = mapper.readValue(String.valueOf(payload), new TypeReference<ChatProtocol<ChatProtocol.Message>>() {}); // JDK8
        ChatProtocol<ChatProtocol.Message> chatProtocol = mapper.readValue(String.valueOf(payload), new TypeReference<>() {
        });
        transformMessage(chatProtocol, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close();
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println(closeStatus.getCode());
        System.out.println("session.getId() = " + session.getId() + "断开");
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String username = path.replace(websocketPrefix + "/", "");
        WebSocketSession remove = USERNAME_SESSION_MAP.remove(username);
        remove.close();
        sendOnlineState(USERNAME_SESSION_MAP.values(), username, false);
        log.info("spring：用户 {} 下线",username);

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void transformMessage(ChatProtocol<ChatProtocol.Message> chatProtocol, WebSocketSession session) throws IOException {
        ChatProtocol.Message message = chatProtocol.getData();
        String json = mapper.writeValueAsString(chatProtocol);
        TextMessage textMessage = new TextMessage(json);
        if ("group".equals(message.getType())) {
            for (WebSocketSession webSocketSession : USERNAME_SESSION_MAP.values()) {
                if (webSocketSession.getId().equals(session.getId())) {
                    continue;
                }
                webSocketSession.sendMessage(textMessage);
            }
            return;
        }
        String toUser = message.getToUser();
        WebSocketSession webSocketSession = USERNAME_SESSION_MAP.get(toUser);
        webSocketSession.sendMessage(textMessage);
    }

    private void sendOnlineState(Collection<WebSocketSession> webSocketSessions, String username, boolean online) {
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, online);
        try {
            for (WebSocketSession webSocketSession : webSocketSessions) {
                webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsBytes(chatProtocol)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pushUserList(WebSocketSession webSocketSession, Enumeration<String> usernames) throws IOException {
        List<String> usernameList = new ArrayList<>();
        while (usernames.hasMoreElements()) {
            String username = usernames.nextElement();
            usernameList.add(username);
        }
        ChatProtocol<ChatProtocol.User> chatProtocol = MessageUtil.userChatProtocol(usernameList);
        webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsBytes(chatProtocol)));
    }
}

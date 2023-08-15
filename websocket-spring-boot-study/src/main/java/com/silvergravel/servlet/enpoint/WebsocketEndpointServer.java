package com.silvergravel.servlet.enpoint;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.util.MessageUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/28
 */
@ServerEndpoint(value = "/servlet/{username}", subprotocols = {"gravel"})
@Component
public class WebsocketEndpointServer {
    private final static ConcurrentHashMap<String, Session> USERNAME_SESSION_MAP = new ConcurrentHashMap<>();


    private final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) throws Exception {
        Session webSocketSession = USERNAME_SESSION_MAP.remove(username);
        if (webSocketSession != null) {
            ChatProtocol<ChatProtocol.ErrorMessage> chatProtocol = MessageUtil.errorMessageChatProtocol("-1", "账号在别处登录 您被已被挤下线!");
            webSocketSession.getBasicRemote().sendText(mapper.writeValueAsString(chatProtocol));
            webSocketSession.close();
            System.err.printf("当前用户: %s session id 为：%s 被挤下线 时间: %s\n", username, webSocketSession.getId(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
            // 对所有用户发送下线通知
            sendOnlineState(USERNAME_SESSION_MAP.values(), username, false);
        }
        // 对所有用户发送上线通知
        sendOnlineState(USERNAME_SESSION_MAP.values(), username, true);
        pushUserList(session, USERNAME_SESSION_MAP.keys());
        USERNAME_SESSION_MAP.put(username, session);
    }


    @OnMessage
    public void onMessage(String message, Session session) throws IOException{
        ChatProtocol<ChatProtocol.Message> chatProtocol = mapper.readValue(message, new TypeReference<ChatProtocol<ChatProtocol.Message>>() {
        });
        transformMessage(chatProtocol, session);
    }

    @OnClose
    public void onClose(@PathParam("username") String username, Session session) throws IOException {
        Session currentSession = USERNAME_SESSION_MAP.remove(username);
        if (currentSession != null) {
            currentSession.close();
        }
        sendOnlineState(USERNAME_SESSION_MAP.values(), username, false);
        session.close();
    }

    @OnError
    public void onError(@PathParam("username") String username, Session session, Throwable throwable) {
        System.err.println(throwable.getMessage());
    }


    private void transformMessage(ChatProtocol<ChatProtocol.Message> chatProtocol, Session session) throws IOException {
        ChatProtocol.Message message = chatProtocol.getData();
        String json = mapper.writeValueAsString(chatProtocol);
        if ("group".equals(message.getType())) {
            for (Session webSocketSession : USERNAME_SESSION_MAP.values()) {
                if (webSocketSession.getId().equals(session.getId())) {
                    continue;
                }
                webSocketSession.getBasicRemote().sendText(json);
            }
            return;
        }
        String toUser = message.getToUser();
        Session webSocketSession = USERNAME_SESSION_MAP.get(toUser);
        webSocketSession.getBasicRemote().sendText(json);
    }

    private void sendOnlineState(Collection<Session> webSocketSessions, String username, boolean online) {
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, online);
        try {
            for (Session webSocketSession : webSocketSessions) {
                webSocketSession.getBasicRemote().sendText(mapper.writeValueAsString(chatProtocol));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pushUserList(Session webSocketSession, Enumeration<String> usernames) throws IOException {

        List<String> usernameList = new ArrayList<>();
        while (usernames.hasMoreElements()) {
            String username = usernames.nextElement();
            usernameList.add(username);
        }
        ChatProtocol<ChatProtocol.User> chatProtocol = MessageUtil.userChatProtocol(usernameList);
        webSocketSession.getBasicRemote().sendText(mapper.writeValueAsString(chatProtocol));

    }


}

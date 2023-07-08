package com.silvergravel.endpoint;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/1
 */
@ServerEndpoint(value = "/servlet/{id}",subprotocols = {"gravel"})
@Component
public class WebsocketEndpointServer {
    private final static ConcurrentHashMap<Long, Session> ID_SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam("id") Long id, Session session) throws Exception {
        Session webSocketSession = ID_SESSION_MAP.get(id);
        if (webSocketSession != null) {
            webSocketSession.close();
            System.err.printf("当前用户: %d session id 为：%s 被挤下线 时间: %s\n",id, webSocketSession.getId(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));;
        }
        ID_SESSION_MAP.put(id, session);
    }


    @OnMessage
    public void onMessage(@PathParam("id") Long id, String message,Session session) {
        System.out.println(id+": "+message);
    }

    @OnClose
    public void onClose(@PathParam("id") Long id, Session session) throws IOException {
        Session currentSession = ID_SESSION_MAP.get(id);
        if (currentSession != null) {
            currentSession.close();
        }
        session.close();
    }

    @OnError
    public void onError(@PathParam("id") Long id, Session session,Throwable throwable) {
        System.err.println(throwable.getMessage());
    }

}

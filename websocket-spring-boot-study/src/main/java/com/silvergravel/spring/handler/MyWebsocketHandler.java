package com.silvergravel.spring.handler;


import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/28
 */
public class MyWebsocketHandler implements WebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session.getUri());
//        //
//        Session webSocketSession = ID_SESSION_MAP.get(id);
//        if (webSocketSession != null) {
//            webSocketSession.close();
//            System.err.printf("当前用户: %d session id 为：%s 被挤下线 时间: %s\n",id, webSocketSession.getId(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));;
//        }
//        ID_SESSION_MAP.put(id, session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println(session.getUri());
        System.out.println(message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

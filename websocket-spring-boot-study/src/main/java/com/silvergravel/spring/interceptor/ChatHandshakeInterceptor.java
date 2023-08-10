package com.silvergravel.spring.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/28
 */
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private String websocketPrefix;

    public ChatHandshakeInterceptor() {
    }

    public ChatHandshakeInterceptor(String websocketPrefix) {
        this.websocketPrefix = websocketPrefix;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 建立连接前，判断路径是否合法
        String path = request.getURI().getPath();
        boolean matches = path.replace(websocketPrefix, "").matches("^/[0-9A-z\u4e00-\u9af5]{1,10}");
        if (matches) {
            return true;
        }
        response.getBody().write("参数不合法".getBytes(StandardCharsets.UTF_8));
        System.err.println("参数不合法");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}

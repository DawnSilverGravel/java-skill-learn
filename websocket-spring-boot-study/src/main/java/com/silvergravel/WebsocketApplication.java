package com.silvergravel;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author DwanStar
 */
@SpringBootApplication
@EnableWebSocket
public class WebsocketApplication {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
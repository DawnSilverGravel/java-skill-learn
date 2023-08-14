package com.silvergravel;

import com.silvergravel.netty.config.NettyWebsocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author DawnStar
 */
@SpringBootApplication
@EnableWebSocket
@EnableAsync
public class WebsocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebsocketApplication.class, args);
    }

    @Resource
    private NettyWebsocketServer nettyWebsocketServer;

    @PostConstruct
    public void init() {
        nettyWebsocketServer.init();
    }
}

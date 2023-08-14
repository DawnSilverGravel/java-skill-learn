
package com.silvergravel.netty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * description:
 *
 * @author DawnStar
 * date: 2023/8/14
 */
@Configuration
public class NettyConfiguration {
    @Value("${netty.port}")
    private Integer nettyPort;

    @Value("${netty.websocket-path}")
    private String websocketPath;


    @Bean
    public NettyWebsocketServer nettyWebsocketServer() {
        return new NettyWebsocketServer(nettyPort,websocketPath);
    }
}

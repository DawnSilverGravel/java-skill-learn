package com.silvergravel.netty.config;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/7/4
 */
public class BeforeWebsocketHandler extends ChannelInboundHandlerAdapter {
    private final String websocketPrefix;
    public BeforeWebsocketHandler(String prefix) {
        this.websocketPrefix = prefix;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(websocketPrefix);
    }
}


package com.silvergravel.netty.handler;


import com.silvergravel.netty.service.NettyWebsocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author DawnStar
 * @date 2022/10/11
 */
public class WebsocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ExecutorService executorService = new ThreadPoolExecutor(8, 8, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(500),new DefaultThreadFactory("WebsocketServerHandler"), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.println(textWebSocketFrame.text());
        String msg = textWebSocketFrame.text();
        NettyWebsocketService.transformMessage(msg, channelHandlerContext);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        // 等待一会，推送列表消息,这里可能不稳定
//        executorService.execute(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(200);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            NettyWebsocketService.pushUserList(ctx);
//        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyWebsocketService.removeUser(ctx);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手成功之后推送协议
            NettyWebsocketService.pushUserList(ctx);
        }

    }
}

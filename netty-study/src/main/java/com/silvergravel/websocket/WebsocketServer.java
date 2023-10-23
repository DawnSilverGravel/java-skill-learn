package com.silvergravel.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @author DawnStar
 * @since : 2023/10/15
 */
public class WebsocketServer {
    public static void main(String[] args) {
        new WebsocketServer().init();
    }

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        WebSocketServerProtocolConfig config = WebSocketServerProtocolConfig.newBuilder()
                .allowExtensions(true)
                .dropPongFrames(false)
                .subprotocols(null)
                // 默认最大值为65536
                .maxFramePayloadLength(65536)
                // /websocket 为端点
                .websocketPath("/websocket")
                .build();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 200)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                /*
                                 * 相当于
                                 * .addLast(new HttpRequestDecoder())
                                 * .addLast(new HttpResponseEncoder())
                                 **/
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(8192))
                                .addLast(new WebSocketServerProtocolHandler(config))
                                .addLast(new SimpleChannelInboundHandler<WebSocketFrame>() {

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
                                        if (msg instanceof TextWebSocketFrame textWebSocketFrame) {
                                            String text = textWebSocketFrame.text();
                                            System.out.println("内容："+text);
                                            ctx.writeAndFlush(new TextWebSocketFrame("服务发送" + text));
                                        }
                                    }

                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
                                            System.out.println("握手完成");
                                        }

                                    }
                                });

                    }
                });

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8096).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

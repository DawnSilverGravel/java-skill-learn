package com.silvergravel.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author DawnStar
 * @since : 2023/10/15
 */
public class WebsocketClient {
    public static void main(String[] args) throws URISyntaxException {
        new WebsocketClient().init();
    }

    public void init() throws URISyntaxException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        WebSocketClientProtocolConfig config = WebSocketClientProtocolConfig.newBuilder().
                webSocketUri(new URI("ws://localhost:8096/websocket"))
                .generateOriginHeader(true)
                .version(WebSocketVersion.V13)
                // 使用绝对路径进行升级协议，默认为false
                .absoluteUpgradeUrl(false)
                // 是否丢弃 PONG 帧，默认true
                .dropPongFrames(false)
                .allowExtensions(true)
                .subprotocol(null)
                .build();

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpClientCodec())
                                // HttpObjectAggregator必须，否则无法正确解析响应
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new HttpObjectAggregator(8192))
                                .addLast(new WebSocketClientProtocolHandler(config))
                                .addLast(new SimpleChannelInboundHandler<WebSocketFrame>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
                                        if (msg instanceof TextWebSocketFrame frame) {
                                            System.out.println(frame.text());
                                        }
                                    }

                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        if (evt.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
                                            System.out.println("握手完成");
                                            ctx.writeAndFlush(new PingWebSocketFrame());
                                            ctx.writeAndFlush(new TextWebSocketFrame("人间失格"));
                                        }
                                    }
                                });
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect("localhost", 8096).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}

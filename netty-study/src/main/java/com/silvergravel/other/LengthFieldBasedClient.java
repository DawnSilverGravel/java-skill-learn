package com.silvergravel.other;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class LengthFieldBasedClient {
    public static void main(String[] args) {
        new LengthFieldBasedClient().init();
    }
    private void init() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                // 只有1, 2, 3, 4, and 8 是被允许
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new ChannelDuplexHandler() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        byte[] bytes = "人们总是对自己所处的地方不满意".getBytes(StandardCharsets.UTF_8);
                                        ByteBuf buffer = ctx.alloc().buffer(bytes.length);
                                        buffer.writeBytes(bytes);
                                        try {
                                            for (int i = 0; ; i++) {
                                                buffer.retain();
                                                buffer.writeBytes(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
                                                TimeUnit.SECONDS.sleep(1);
                                                ctx.writeAndFlush(buffer);
                                            }
                                        } finally {
                                            ReferenceCountUtil.safeRelease(buffer);
                                        }
                                    }
                                });
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect("localhost", 8094).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}

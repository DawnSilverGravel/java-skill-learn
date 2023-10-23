package com.silvergravel.other;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author DawnStar
 * @since : 2023/10/22
 */
public class FixedLengthClient {
    public static void main(String[] args) {
        new FixedLengthClient().init();
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
                                .addLast(new ChannelDuplexHandler() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        byte[] bytes = "你们根本不像我的玫瑰，你们现在什么也不是。".getBytes(StandardCharsets.UTF_8);
                                        System.out.println(bytes.length);
                                        ByteBuf buffer = ctx.alloc().buffer(bytes.length);
                                        buffer.writeBytes(bytes);
                                        try {
                                            for (int i = 0; ; i++) {
                                                buffer.retain();
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
            ChannelFuture future = bootstrap.connect("localhost", 8093).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}

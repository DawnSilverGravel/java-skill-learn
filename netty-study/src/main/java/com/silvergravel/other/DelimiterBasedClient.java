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
public class DelimiterBasedClient {
    public static void main(String[] args) {
        new DelimiterBasedClient().init();
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
                                        byte[] bytes = "你无非是个孩子，和其他成千上万个孩子没有什么区别。".getBytes(StandardCharsets.UTF_8);
                                        ByteBuf buffer = ctx.alloc().buffer(bytes.length);
                                        buffer.writeBytes(bytes);
                                        try {
                                            for (int i = 0; ; i++) {
                                                buffer.retain();
                                                buffer.writeBytes(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
                                                buffer.markWriterIndex();
                                                buffer.writeBytes("$$".getBytes(StandardCharsets.UTF_8));
                                                ctx.writeAndFlush(buffer);
                                                TimeUnit.SECONDS.sleep(1);
                                                buffer.resetWriterIndex();
                                            }
                                        } finally {
                                            ReferenceCountUtil.safeRelease(buffer);
                                        }
                                    }
                                });
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect("localhost", 8092).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}

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
public class LineBasedClient {
    public static void main(String[] args) {
        new LineBasedClient().init();
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
                                        byte[] bytes = "你会得到你的日落。我会命令太阳落下".getBytes(StandardCharsets.UTF_8);
                                        ByteBuf buffer = ctx.alloc().buffer(bytes.length);
                                        buffer.writeBytes(bytes);
                                        try {
                                            for (int i = 0; ; i++) {
                                                buffer.retain();
                                                buffer.writeBytes(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
                                                buffer.markWriterIndex();
                                                if (i %2== 0) {
                                                    buffer.writeBytes("\n".getBytes(StandardCharsets.UTF_8));
                                                }else {
                                                    buffer.writeBytes("\r\n".getBytes(StandardCharsets.UTF_8));
                                                }
                                                TimeUnit.SECONDS.sleep(1);
                                                ctx.writeAndFlush(buffer);
                                                buffer.resetReaderIndex();
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
            ChannelFuture future = bootstrap.connect("localhost", 8095).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

}

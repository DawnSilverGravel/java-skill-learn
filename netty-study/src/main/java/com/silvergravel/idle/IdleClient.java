package com.silvergravel.idle;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/10
 */
public class IdleClient {
    public static void main(String[] args) {
        IdleClient idleClient = new IdleClient();
        idleClient.init();
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
                                .addLast(new IdleStateHandler(10, 10, 10))
                                .addLast(new ClientHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect("localhost", 8091).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static class ClientHandler extends ChannelDuplexHandler {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
            ByteBuf byteBuf = (ByteBuf) msg;
            int length = byteBuf.readableBytes();
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            String s = new String(bytes, StandardCharsets.UTF_8);
            System.out.println(s);

            for (int i = 0; i < 10; i++) {
                byteBuf.writeBytes(("去年，平安无事。\n前年，平安无事。\n在那以前，也平安无事。"+i).getBytes(StandardCharsets.UTF_8));
                byteBuf.retain();
                ctx.writeAndFlush(byteBuf);
            }
            ReferenceCountUtil.safeRelease(msg);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            // 处理空闲事件
            if (evt instanceof IdleStateEvent event) {
                if (event.state() == IdleState.READER_IDLE) {
                    System.err.println("Client 读空闲");;
                    ctx.close();
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println(cause.getMessage());
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            byte[] bytes = "客户端首次发送消息".getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = Unpooled.buffer(bytes.length);
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);
        }
    }
}

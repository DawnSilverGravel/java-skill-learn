package com.silvergravel.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author DawnStar
 * @since : 2023/10/24
 */
public class CustomerClient {
    public static void main(String[] args) {
        new CustomerClient().init();
    }

    public void init() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DataProtocolDecoder())
                                .addLast(new DataProtocolEncoder())
                                .addLast(new ObjectToDataProtocolEncoder())
                                .addLast(new DataProtocolToObjectDecoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        User user = new User();
                                        user.setUsername("SilverGravel");
                                        user.setAge(24);
                                        System.out.println(user);
                                        ctx.writeAndFlush(user);
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                        ReferenceCountUtil.safeRelease(msg);
                                    }
                                });
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect("localhost", 8088).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}

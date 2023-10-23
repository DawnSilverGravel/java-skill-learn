package com.silvergravel.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author DawnStar
 * @since : 2023/10/15
 */
public class CustomerServer {
    public static void main(String[] args) {
        new CustomerServer().init();
    }

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 200)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DataProtocolDecoder())
                                .addLast(new DataProtocolEncoder())
                                .addLast(new ObjectToDataProtocolEncoder())
                                .addLast(new DataProtocolToObjectDecoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                        if (msg instanceof User user) {
                                            System.out.println("姓名"+user.getUsername());
                                            System.out.println("年龄"+user.getAge());
                                            // 返回信息
                                            Info info = new Info();
                                            info.setJob("Java工程师");
                                            info.setContent("大鹏一日同风起，扶摇直上九万里");
                                            ctx.writeAndFlush(info);
                                        }
                                    }
                                });

                    }
                });

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

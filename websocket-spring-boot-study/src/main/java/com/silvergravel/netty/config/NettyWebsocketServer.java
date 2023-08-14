
package com.silvergravel.netty.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PreDestroy;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/7/4
 */
public class NettyWebsocketServer {


    private final int port;

    private final String websocketPath;

    /**
     * bossGroup 用于接收传入连接
     */
    private final EventLoopGroup bossGroup;
    /**
     * workGroup 用于处理接受连接的流量
     * bossGroup 接受连接成注册到workGroup
     */
    private final EventLoopGroup workGroup;


    public NettyWebsocketServer(int nettyPort,String websocketPath) {
        this.port = nettyPort;
        this.websocketPath = websocketPath;
        // NioEventLoopGroup 是一个处理I/O操作的多线程事件循环
        // 为不同类型的传输提供各种实现
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
    }

    @Async
    public void init() {
        // Bootstrap是一个设置服务器的助手类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture;
        try {
            channelFuture = serverBootstrap.group(bossGroup, workGroup)
                    // 指定NioServerSocketChannel类用于实例化一个新的
                    // Channel实例用于接受连接
                    .channel(NioServerSocketChannel.class)
                    // 是一个特殊的处理类，自定义添加管道，处理更多的程序
                    .childHandler(new WebsocketServerInitializer(websocketPath))
                    // 服务端接受连接队列的长度，若果队列已满，客户端连接将被拒绝
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // Socket 参数，连接保活，默认值为false，启用该功能时
                    // TCP会主动探测空闲连接的有效性
                    // 可以将此功能视为TCP的心跳机制
                    // 默认的心跳间隔是7200s，Netty默认关闭该功能
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(port).sync();
            // 等待直到服务器关闭套接字
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 销毁容器前，停止Netty
     */
    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }


}

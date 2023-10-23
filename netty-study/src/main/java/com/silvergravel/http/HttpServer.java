package com.silvergravel.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/15
 */
public class HttpServer {
    public static void main(String[] args) {
        new HttpServer().init();
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
                                /*
                                 * 相当于
                                 * .addLast(new HttpRequestDecoder())
                                 * .addLast(new HttpResponseEncoder())
                                 **/
                                .addLast(new HttpServerCodec())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        if (msg instanceof DefaultHttpRequest request) {
                                            System.out.println(request);
                                            // 生成响应
                                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                            String content = "犹如落叶一般打着旋，被吸入深水中";
                                            response.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
                                            ctx.writeAndFlush(response);
                                        }
                                        if (msg instanceof DefaultLastHttpContent lastHttpContent) {
                                            byte[] bytes = ByteBufUtil.getBytes(lastHttpContent.content());
                                            System.out.println(new String(bytes,StandardCharsets.UTF_8));
                                        }
                                        ReferenceCountUtil.safeRelease(msg);
                                    }
                                });

                    }
                });

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

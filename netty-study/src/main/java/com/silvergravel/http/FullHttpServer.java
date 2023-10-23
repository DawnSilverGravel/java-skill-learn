package com.silvergravel.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class FullHttpServer {
    public static void main(String[] args) {
        new FullHttpServer().init();
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
                                // 聚合HttpMessage与HttpContent
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new HttpObjectAggregator(8192))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                        if (msg instanceof FullHttpRequest request) {
                                            System.out.println(request);
                                            byte[] bytes = ByteBufUtil.getBytes(request.content());
                                            System.out.println(new String(bytes,StandardCharsets.UTF_8));
                                            // 生成响应
                                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                            String content = "犹如落叶一般打着旋，被吸入深水中";
                                            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                                            response.content().writeBytes(contentBytes);
                                            // 需要关闭连接
                                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                        }
                                        ReferenceCountUtil.safeRelease(msg);
                                    }
                                });

                    }
                });

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8089).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

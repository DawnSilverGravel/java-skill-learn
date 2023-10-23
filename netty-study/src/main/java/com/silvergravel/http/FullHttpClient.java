package com.silvergravel.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class FullHttpClient {
    public static void main(String[] args) {
        new FullHttpClient().init();
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
                                /*
                                 * 相当于
                                 * .addLast(new HttpRequestEncoder())
                                 * .addLast(new HttpResponseDecoder())
                                 */
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(8192))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        // 连接成功发送数据
                                        URI silverGravel = URI.create("/silver-gravel?content=中文测试");
                                        // 仅仅 toString中文可能乱码，toASCIIString 则是这样
                                        // SilverGravel%E4%B8%AD%E6%96%87%E6%B5%8B%E8%AF%95
                                        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, silverGravel.toASCIIString());
                                        // 写入内容
                                        request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
                                        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                                        request.headers().set("silver", "gravel");
                                        String content = "生活就像海洋，只有意志坚强的人才能到达彼岸";
                                        // 必须设置长度，不设置 server获取不到content
                                        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.getBytes(StandardCharsets.UTF_8).length);
                                        request.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                                        request.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
                                        ctx.writeAndFlush(request);
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 解析响应
                                        if (msg instanceof FullHttpResponse response) {
                                            System.out.println(response);
                                            byte[] bytes = ByteBufUtil.getBytes(response.content());
                                            System.out.println(new String(bytes,StandardCharsets.UTF_8));
                                        }
                                        ReferenceCountUtil.safeRelease(msg);
                                    }
                                });
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect("localhost", 8089).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}

package com.silvergravel.netty.handler;

import com.silvergravel.netty.config.BeforeWebsocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author DawnStar
 * @date 2022/10/11
 */
public class WebsocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                // 使用http 解码器和编码器
                .addLast(new HttpServerCodec())
                // 使用块方式读写
                .addLast(new ChunkedWriteHandler())
                // 使用http 累加器
                .addLast(new HttpObjectAggregator(8192))
                // 在转换协议之前处理路径,通过路径绑定用户
                .addLast(new BeforeWebsocketHandler("/websocket"))
                // 转换ws协议
                .addLast(new WebSocketServerProtocolHandler("/websocket"))
                // 添加自定义处理器
                .addLast(new WebsocketServerHandler());
    }
}

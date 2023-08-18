
package com.silvergravel.netty.handler;


import com.silvergravel.netty.service.NettyWebsocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * description: 在转换成http协议之前需要需要做的事情
 *
 * @author DawnStar
 * date: 2023/7/4
 */
public class BeforeWebsocketHandler extends ChannelInboundHandlerAdapter {
    private final String websocketPrefix;

    public BeforeWebsocketHandler(String prefix) {
        this.websocketPrefix = prefix;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断 msg 是否为 http request
        String connection = "Upgrade";
        // msg instanceof FullHttpRequest request 为 JDK17 版本写法
        // FullHttpRequest request = (FullHttpRequest) msg; // JDK8 强转
        if (msg instanceof FullHttpRequest request) {
            if (connection.equals(request.headers().get("Connection"))) {
                String regex = "^[0-9A-z\\u4e00-\\u9af5]{2,10}";
                String path = request.uri().replace(websocketPrefix + "/", "");
                String username = URLDecoder.decode(path, StandardCharsets.UTF_8);
                if (username.matches(regex)) {
                    // 添加 建立channel映射
                    NettyWebsocketService.addUser(username, ctx);
                    // 重新设置 request URI 重要
                    request.setUri(websocketPrefix);
                } else {
                    System.err.println("用户名不合法！关闭连接");
                    ctx.close();
                }
            }
        }
        super.channelRead(ctx, msg);
    }
}

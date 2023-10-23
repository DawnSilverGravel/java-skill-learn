package com.silvergravel.other;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/22
 */
public class MessageChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf buf) {
            byte[] bytes = ByteBufUtil.getBytes(buf);
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
        }
        ReferenceCountUtil.safeRelease(msg);
    }
}

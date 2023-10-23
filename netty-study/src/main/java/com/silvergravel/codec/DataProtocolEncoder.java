package com.silvergravel.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class DataProtocolEncoder extends MessageToByteEncoder<DataProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, DataProtocol msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getTypeLength());
        out.writeInt(msg.getJsonLength());
        byte[] bytes = msg.getType().getBytes(StandardCharsets.UTF_8);
        byte[] jsonBytes = msg.getJson().getBytes(StandardCharsets.UTF_8);
        byte[] targetBytes = new byte[bytes.length + jsonBytes.length];
        System.arraycopy(bytes,0,targetBytes,0,bytes.length);
        System.arraycopy(jsonBytes,0,targetBytes,bytes.length,jsonBytes.length);
        out.writeBytes(targetBytes);
    }
}

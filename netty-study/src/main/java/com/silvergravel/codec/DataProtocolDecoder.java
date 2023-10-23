package com.silvergravel.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class DataProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 读取前面两个长度字段
        if (in.readableBytes() < 8) {
            return;
        }
        int typeLength = in.readInt();
        int jsonLength = in.readInt();
        // 标记当前读取数
        in.markReaderIndex();
        if (in.readableBytes() < typeLength + jsonLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] typeBytes = new byte[typeLength];
        in.readBytes(typeBytes);
        byte[] jsonBytes = new byte[jsonLength];
        in.readBytes(jsonBytes);
        DataProtocol dataProtocol = new DataProtocol();
        dataProtocol.setJsonLength(jsonLength);
        dataProtocol.setJsonLength(typeLength);
        dataProtocol.setJson(new String(jsonBytes, StandardCharsets.UTF_8));
        dataProtocol.setType(new String(typeBytes, StandardCharsets.UTF_8));
        out.add(dataProtocol);
    }
}

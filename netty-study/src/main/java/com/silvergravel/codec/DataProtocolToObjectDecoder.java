package com.silvergravel.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class DataProtocolToObjectDecoder extends MessageToMessageDecoder<DataProtocol> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DataProtocol msg, List<Object> out) throws Exception {
        Class<?> aClass = Class.forName(msg.getType());
        ObjectMapper objectMapper = new ObjectMapper();
        Object value = objectMapper.readValue(msg.getJson(), aClass);
        out.add(value);
    }
}

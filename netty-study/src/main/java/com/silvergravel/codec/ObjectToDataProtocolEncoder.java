package com.silvergravel.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
public class ObjectToDataProtocolEncoder extends MessageToMessageEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(msg);
        DataProtocol dataProtocol = DataProtocol.createProtocol(msg.getClass(), json);
        out.add(dataProtocol);
    }
}

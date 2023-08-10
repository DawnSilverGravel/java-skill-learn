package com.silvergravel.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author DawnStar
 * @date 2022/10/11
 */
public class WebsocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.print("websocket 8092: ");
        System.out.println(textWebSocketFrame.text());
        String msg = textWebSocketFrame.text();
//        Protocol protocol = JSONUtil.toBean(msg,Protocol.class);
//        if(Constant.CONNECT.equals(protocol.getType())){
//            protocol.setContent("服务器回传:"+protocol.getContent());
//            channelHandlerContext.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(protocol)));
//        }
//        if(Constant.CONNECTS.equals(protocol.getType())){
//            String[] split = protocol.getContent().split(Constant.CONNECT_SPLIT);
//
//        }
        // 解析发过来的数据报文,webscoketProtocol是自定义的协议字段类
//        WebSocketProtocol webSocketProtocol = JSONUtil.toBean(msg,WebSocketProtocol.class);
//        if (!checkProtocol(webSocketProtocol)) {
//            channelHandlerContext.writeAndFlush(JSONUtil.toJsonStr(WebsocketUtil.errMessage(WebsocketTypeEnum.PROTOCOL_ERR)));
//        }
        System.out.println("simple:"+channelHandlerContext.channel().getClass());

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("8092持续连接:"+ctx.channel().id().asLongText());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("8092失活:"+ctx.channel().id().asLongText());
    }
}

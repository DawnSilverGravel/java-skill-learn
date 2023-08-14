
package com.silvergravel.netty.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.util.MessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/8/14
 */
public class NettyWebsocketService {


    private final static ConcurrentHashMap<String, ChannelHandlerContext> NETTY_USERNAME_CHANNEL_MAP = new ConcurrentHashMap<>();

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private NettyWebsocketService() {

    }

    public static void addUser(String username, ChannelHandlerContext channel) {
        // 先拿之前的values
        Collection<ChannelHandlerContext> contexts = NETTY_USERNAME_CHANNEL_MAP.values();
        // 将指定的 username 覆盖
        ChannelHandlerContext lastChannel = NETTY_USERNAME_CHANNEL_MAP.put(username, channel);
        if (lastChannel != null) {
            // 下线通知
            ChatProtocol<ChatProtocol.ErrorMessage> chatProtocol = MessageUtil.errorMessageChatProtocol("-1", "您被挤下线");
            try {
                lastChannel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(chatProtocol)));
                lastChannel.close();
                sendOnlineState(contexts, username, false);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        // 发送上线通知
        sendOnlineState(contexts, username, true);
    }


    public static void remove(ChannelHandlerContext ctx) {
        String username = null;
        for (Map.Entry<String, ChannelHandlerContext> entry : NETTY_USERNAME_CHANNEL_MAP.entrySet()) {
            boolean equals = entry.getValue().channel().id().asLongText().equals(ctx.channel().id().asLongText());
            if (equals) {
                username = entry.getKey();
                break;
            }
        }
        if (username == null) {
            return;
        }
        NETTY_USERNAME_CHANNEL_MAP.remove(username);
        sendOnlineState(NETTY_USERNAME_CHANNEL_MAP.values(), username, false);
    }

    private static void sendOnlineState(Collection<ChannelHandlerContext> contexts, String username, boolean online) {
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, online);
        for (ChannelHandlerContext context : contexts) {
            try {
                context.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(chatProtocol)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void pushUserList(ChannelHandlerContext channel, Enumeration<String> keys, String currentUsername) {
        List<String> usernames = new ArrayList<>();
        while (keys.hasMoreElements()) {
            String element = keys.nextElement();
            if (element.equals(currentUsername)) {
                continue;
            }
            usernames.add(element);
        }
        ChatProtocol<ChatProtocol.User> chatProtocol = MessageUtil.userChatProtocol(usernames);
        try {
            channel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(chatProtocol)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void transformMessage(String payload, ChannelHandlerContext currentContext) throws IOException {
        ChatProtocol<ChatProtocol.Message> chatProtocol = MAPPER.readValue(payload, new TypeReference<ChatProtocol<ChatProtocol.Message>>() {
        });
        ChatProtocol.Message message = chatProtocol.getData();
        if ("group".equals(message.getType())) {
            for (ChannelHandlerContext context : NETTY_USERNAME_CHANNEL_MAP.values()) {
                if (context.channel().id().equals(currentContext.channel().id())) {
                    continue;
                }
                context.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(chatProtocol)));
            }
            return;
        }
        String toUser = message.getToUser();
        ChannelHandlerContext channelHandlerContext = NETTY_USERNAME_CHANNEL_MAP.get(toUser);
        channelHandlerContext.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(chatProtocol)));
    }


    public static void pushUserList(ChannelHandlerContext ctx) {
        List<String> usernames = new ArrayList<>();
        for (Map.Entry<String, ChannelHandlerContext> entry : NETTY_USERNAME_CHANNEL_MAP.entrySet()) {
            boolean equals = entry.getValue().channel().id().asLongText().equals(ctx.channel().id().asLongText());
            if (equals) {
                continue;
            }
            usernames.add(entry.getKey());
        }
        ChatProtocol<ChatProtocol.User> chatProtocol = MessageUtil.userChatProtocol(usernames);
        try {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(chatProtocol)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}



package com.silvergravel.stomp.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.stomp.constant.ChatConstant;
import com.silvergravel.stomp.service.StompChatService;
import com.silvergravel.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/29
 */
@Controller
@Slf4j
public class StompController {

    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @Value("${stomp.user-destination}")
    private String userDestination;

    private final ObjectMapper mapper = new ObjectMapper();


    @MessageMapping("/event-handle")
    public void eventHandle(String payload) throws JsonProcessingException {
        String command = payload.substring(0, payload.indexOf(","));
        String username = payload.substring(payload.indexOf(",") + 1);
        if (ChatConstant.ONLINE.equals(command)) {
            ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, true);
            messagingTemplate.convertAndSend("/topic" + userDestination, mapper.writeValueAsString(chatProtocol));
            return;
        }
        if (ChatConstant.OFFLINE.equals(command)) {
            ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, false);
            messagingTemplate.convertAndSend("/topic" + userDestination, mapper.writeValueAsString(chatProtocol));
            return;
        }
        if (ChatConstant.USER_LIST.equals(command)) {
            List<String> users = StompChatService.getUsersExceptUser(username);
            ChatProtocol<ChatProtocol.User> userChatProtocol = MessageUtil.userChatProtocol(users);
            messagingTemplate.convertAndSendToUser(username, userDestination, mapper.writeValueAsString(userChatProtocol));
        }

        if (ChatConstant.ERROR_MESSAGE.equals(command)) {
            ChatProtocol<ChatProtocol.ErrorMessage> chatProtocol = MessageUtil.errorMessageChatProtocol("-1", "您被挤下线");
            messagingTemplate.convertAndSendToUser(username, userDestination, mapper.writeValueAsString(chatProtocol));
        }
    }


    @MessageMapping("/forced-offline")
    public void forcedOffline(String username) throws JsonProcessingException {
        log.warn("stomp用户：{} 被挤下线", username);
        ChatProtocol<ChatProtocol.ErrorMessage> chatProtocol = MessageUtil.errorMessageChatProtocol("-1", "您被挤下线");
        messagingTemplate.convertAndSendToUser(username, userDestination, mapper.writeValueAsString(chatProtocol));
    }


    /**
     * 向其他用户发送 指定用户上线信息
     *
     * @param username 用户名称
     */
    @MessageMapping("/online")
    public void onlineState(String username) throws JsonProcessingException {
        log.info("stomp连接：{} 上线", username);
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, true);
        messagingTemplate.convertAndSend("/topic" + userDestination, mapper.writeValueAsString(chatProtocol));
    }

    /**
     * 向其他用户范松 指定用户离线信息
     *
     * @param username 用户名称
     * @throws JsonProcessingException
     */
    @MessageMapping("/offline")
    public void offlineState(String username) throws JsonProcessingException {
        log.info("stomp连接：{} 下线", username);
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(username, false);
        messagingTemplate.convertAndSend("/topic" + userDestination, mapper.writeValueAsString(chatProtocol));
    }

    /**
     * 获取所有在线用户
     *
     * @param username 指定用户
     */
    @MessageMapping("/user-list")
    public void sendUserList(String username) {
        List<String> users = StompChatService.getUsersExceptUser(username);
        ChatProtocol<ChatProtocol.User> userChatProtocol = MessageUtil.userChatProtocol(users);
        try {
            messagingTemplate.convertAndSendToUser(username, userDestination, mapper.writeValueAsString(userChatProtocol));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/message")
    public void message(String payload) throws JsonProcessingException {
        //  ChatProtocol<ChatProtocol.Message> chatProtocol = mapper.readValue(String.valueOf(payload), new TypeReference<ChatProtocol<ChatProtocol.Message>>() { });  // JDK8
        ChatProtocol<ChatProtocol.Message> chatProtocol = mapper.readValue(String.valueOf(payload), new TypeReference<>() {
        });
        ChatProtocol.Message message = chatProtocol.getData();
        String chatType = "group";
        if (chatType.equals(message.getType())) {
            messagingTemplate.convertAndSend("/topic" + userDestination, payload);
            return;
        }
        String toUser = message.getToUser();
        messagingTemplate.convertAndSendToUser(toUser, userDestination, payload);
    }


    /**
     * 订阅 topic/greetings 主题的前端 接受两次消息
     *
     * @param message
     * @return
     * @throws Exception
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        System.err.println(message);
        messagingTemplate.convertAndSend("/topic/greetings", message);
        return message;
    }
}

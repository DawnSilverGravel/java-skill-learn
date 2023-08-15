
package com.silvergravel.stomp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.stomp.service.StompChatService;
import com.silvergravel.util.MessageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
public class StompController {

    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @Value("${stomp.user-destination}")
    private String userDestination;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 获取用户在线状态
     * @param message 内容
     */
    @MessageMapping("/online")
    public void onlineState(String message) throws JsonProcessingException {
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(message, true);
        messagingTemplate.convertAndSend("/topic/"+userDestination,mapper.writeValueAsString(chatProtocol));
    }

    @MessageMapping("/offline")
    public void offlineState(String message) throws JsonProcessingException {
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = MessageUtil.onlineStateProtocol(message, false);
        messagingTemplate.convertAndSend("/topic"+userDestination,mapper.writeValueAsString(chatProtocol));
    }

    /**
     * 获取所有在线用户
     */
    @MessageMapping("/user-list")
    public void sendUserList(String username){
        List<String> users = StompChatService.getUsers(username);
        ChatProtocol<ChatProtocol.User> userChatProtocol = MessageUtil.userChatProtocol(users);
        System.out.println(username);
        try {
            messagingTemplate.convertAndSendToUser(username,userDestination,mapper.writeValueAsString(userChatProtocol));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/message")
    public void message(String message) {

        System.out.println(message);
    }


    /**
     * 订阅 topic/greetings 主题的前端 接受两次消息
     * @param message
     * @return
     * @throws Exception
     */
    @MessageMapping("/hello")
    @SendToUser("/topic/greetings")
    public String greeting(String message) throws Exception {
        System.err.println(message);
        messagingTemplate.convertAndSend("/topic/greetings",message);
        return message;
    }
}

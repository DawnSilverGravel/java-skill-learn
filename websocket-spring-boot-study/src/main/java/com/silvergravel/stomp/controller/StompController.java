
package com.silvergravel.stomp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.protocol.ProtocolTypeEnum;
import com.silvergravel.stomp.service.StompChatService;
import com.silvergravel.util.MessageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

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
    public String onlineState(String message) {
        return message;
    }

    @MessageMapping("/offline")
    public String offlineState(String message) {
        return message;
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



    @MessageMapping("/hello")
//    @SendToUser("/444/notice")
    public String greeting(String message) throws Exception {
        System.err.println(message);
        messagingTemplate.convertAndSend("/topic/greetings",message);
        messagingTemplate.convertAndSendToUser(message,"/notice",message);
        return message;
    }
}

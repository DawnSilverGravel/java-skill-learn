package com.silvergravel.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvergravel.protocol.ChatProtocol;
import com.silvergravel.protocol.ProtocolTypeEnum;

import java.util.Collection;
import java.util.List;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/8/10
 */
public class MessageUtil {
    private final static ObjectMapper mapper = new ObjectMapper();

    private MessageUtil() {
    }

    public static ChatProtocol<ChatProtocol.OnlineState> onlineStateProtocol(String username, boolean online) {
        ChatProtocol.OnlineState onlineState = new ChatProtocol.OnlineState();
        onlineState.setOnline(online);
        onlineState.setUsername(username);
        ChatProtocol<ChatProtocol.OnlineState> chatProtocol = new ChatProtocol<>();
        chatProtocol.setData(onlineState);
        chatProtocol.setType(ProtocolTypeEnum.ONLINE_STATE.getType());
        return chatProtocol;
    }

    public static ChatProtocol<ChatProtocol.User> userChatProtocol(List<String> usernameList) {
        ChatProtocol.User user = new ChatProtocol.User();
        user.setUsernames( usernameList);
        ChatProtocol<ChatProtocol.User> chatProtocol = new ChatProtocol<>();
        chatProtocol.setType(ProtocolTypeEnum.ONLINE_UER_LIST.getType());
        chatProtocol.setData(user);
        return chatProtocol;
    }

    public static ChatProtocol<ChatProtocol.ErrorMessage> errorMessageChatProtocol(String errorCode, String errorMessageContent) {
        ChatProtocol.ErrorMessage errorMessage = new ChatProtocol.ErrorMessage();
        errorMessage.setErrorCode(errorCode);
        errorMessage.setErrorMessage(errorMessageContent);
        ChatProtocol<ChatProtocol.ErrorMessage> chatProtocol = new ChatProtocol<>();
        chatProtocol.setData(errorMessage);
        chatProtocol.setType(ProtocolTypeEnum.ERROR_MESSAGE.getType());
        return chatProtocol;
    }

}

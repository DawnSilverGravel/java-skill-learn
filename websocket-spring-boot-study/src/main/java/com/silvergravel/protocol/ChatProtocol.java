package com.silvergravel.protocol;

import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/29
 */
@Data
public class ChatProtocol<T extends GravelProtocol> {
    /**
     * 协议类型
     */
    private String type;

    private T data;

    @Data
    public static class ErrorMessage implements GravelProtocol{
        private String errorCode;
        private String errorMessage;
    }

    @Data
    public static class OnlineState implements GravelProtocol{
        /**
         * 在线状态
         */
        private Boolean online;

        /**
         * 用户名
         */
        private String username;

    }


    @Data
    public static class Message implements GravelProtocol {
        /**
         * 发送的用户，用户不能为null 前端以及后端都做判断
         */
        private String fromUser;
        /**
         * 指定用户，默认null为群聊
         */
        private String toUser;

        private String messageContent;

        /**
         *
         * group private
         */
        private String type;
    }

    @Data
    public static class User implements GravelProtocol{
        private List<String> usernames;
    }

}

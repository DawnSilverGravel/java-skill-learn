package com.silvergravel.protocol;

/**
 * description:
 *
 * @author DawnStar
 * 2023/8/9
 */
public enum ProtocolTypeEnum {
    /**
     * 协议类型
     */
    ONLINE_STATE("online_state","在线状态"),
    ONLINE_UER_LIST("user_list","在线用户"),
    ERROR_MESSAGE("error_message","错误消息"),
    MESSAGE("message","消息"),;
    private final String type;
    private final String description;

    ProtocolTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}

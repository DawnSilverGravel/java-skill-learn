package com.silvergravel.stomp.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/8/15
 */
public class StompChatService {

    private final static ConcurrentHashMap<String, String> STOMP_USERNAME_CHANNEL_MAP = new ConcurrentHashMap<>();

    private StompChatService() {

    }

    public static String addUser(String username, String sessionId) {
        return STOMP_USERNAME_CHANNEL_MAP.put(username, sessionId);
    }

    public static List<String> getUsers(String username) {
        Enumeration<String> keys = STOMP_USERNAME_CHANNEL_MAP.keys();
        List<String> usernames = new ArrayList<>();
        while (keys.hasMoreElements()) {
            String element = keys.nextElement();
            if (element.equals(username)) {
                continue;
            }
            usernames.add(element);
        }
        return usernames;
    }


}

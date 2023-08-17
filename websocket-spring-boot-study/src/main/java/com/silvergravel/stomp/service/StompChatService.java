
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

    private final static ConcurrentHashMap<String, String> STOMP_USERNAME_SESSION_ID_MAP = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, String> STOMP_SESSION_ID_USERNAME_MAP = new ConcurrentHashMap<>();

    private StompChatService() {
    }

    public static synchronized String addUser(String username, String sessionId) {
        STOMP_SESSION_ID_USERNAME_MAP.put(sessionId, username);
        return STOMP_USERNAME_SESSION_ID_MAP.put(username, sessionId);
    }

    public static List<String> getUsersExceptUser(String username) {
        Enumeration<String> keys = STOMP_USERNAME_SESSION_ID_MAP.keys();
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


    public static String getUserName(String sessionId) {
        return STOMP_SESSION_ID_USERNAME_MAP.get(sessionId);
    }

    public synchronized static String  removeUser(String sessionId) {
        String username = STOMP_SESSION_ID_USERNAME_MAP.remove(sessionId);
        if (username == null) {
            return null;
        }
        // 如果当前的key值相同且value相同则认为是自动下线而不是挤退下线
        STOMP_USERNAME_SESSION_ID_MAP.remove(username,sessionId);
        return username;
    }
}

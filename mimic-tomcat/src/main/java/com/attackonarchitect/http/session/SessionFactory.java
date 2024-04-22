package com.attackonarchitect.http.session;

import com.attackonarchitect.utils.RandomUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 会话工厂
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/22
 * @since 1.8
 **/
public abstract class SessionFactory {
    private final static Map<String, MTSession> sessions = new HashMap<>();

    /**
     * 生成随机编号
     */
    protected synchronized static String generateId() {
        byte[] bytes = new byte[16];
        RandomUtil.nextBytes(bytes);

        StringBuilder ret = new StringBuilder();
        for (byte aByte : bytes) {
            byte b1 = (byte) ((aByte & 0xf0) >> 4);
            byte b2 = (byte) (aByte & 0x0f);
            if (b1 < 10)
                ret.append((char) ('0' + b1));
            else
                ret.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                ret.append((char) ('0' + b2));
            else
                ret.append((char) ('A' + (b2 - 10)));
        }
        return ret.toString();
    }

    /**
     * 创建一个session并放入session容器
     * @return
     */
    public static MTSession createSession() {
        String id;
        do {
            id = generateId();
        } while (sessions.containsKey(id));
        MTSession session = new Session(id);
        sessions.put(id, session);
        return session;
    }

    /**
     * 获取指定会话
     * @param sessionId 会话编号
     * @return
     */
    public static MTSession getSession(final String sessionId) {
        MTSession session = sessions.get(sessionId);
        if (Objects.isNull(session)) {
            synchronized (sessions) {
                System.out.println("会话【" + sessionId + "】已失效, 重新创建...");
                sessions.putIfAbsent(sessionId, new Session(sessionId));
                session = getSession(sessionId);
            }
        }
        return session;
    }

    public static final String SESSION_NAME = "jsessionid";
}

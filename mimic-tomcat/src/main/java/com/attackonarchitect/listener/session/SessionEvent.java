package com.attackonarchitect.listener.session;

import com.attackonarchitect.http.session.MTSession;
import com.attackonarchitect.listener.Event;

import java.util.EventObject;
import java.util.StringJoiner;

/**
 * {@linkplain com.attackonarchitect.http.session.MTSession 会话}事件
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/14
 * @since 1.8
 **/
public final class SessionEvent extends EventObject implements Event {
    private final MTSession session;

    private final String type;

    private final Object data;

    public SessionEvent(MTSession session, String type, Object data) {
        super(session);
        this.session = session;
        this.type = type;
        this.data = data;
    }

    public MTSession getSession() {
        return session;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SessionEvent.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("session=" + session)
                .toString();
    }
}

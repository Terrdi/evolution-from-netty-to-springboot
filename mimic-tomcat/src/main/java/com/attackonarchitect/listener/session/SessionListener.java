package com.attackonarchitect.listener.session;

import com.attackonarchitect.listener.EventListener;

/**
 * {@linkplain com.attackonarchitect.http.session.MTSession 会话}事件监听器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/14
 * @since 1.8
 **/
public interface SessionListener extends EventListener {
    void sessionEvent(SessionEvent event);
}

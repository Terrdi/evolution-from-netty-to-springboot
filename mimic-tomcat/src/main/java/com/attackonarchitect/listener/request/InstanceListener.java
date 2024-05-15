package com.attackonarchitect.listener.request;

import com.attackonarchitect.listener.EventListener;

/**
 * Servlet事件的监听器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/14
 * @since 1.8
 **/
public interface InstanceListener extends EventListener {
    void instanceEvent(InstanceEvent event);
}

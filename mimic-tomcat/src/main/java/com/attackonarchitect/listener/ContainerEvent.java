package com.attackonarchitect.listener;

import com.attackonarchitect.context.Container;

import java.util.EventObject;
import java.util.StringJoiner;

/**
 * 容器事件
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/14
 * @since 1.8
 **/
public class ContainerEvent extends EventObject implements Event {
    private final Container container;

    private final String type;

    private final Object data;

    public ContainerEvent(Container source, String type, Object data) {
        super(source);
        this.data = data;
        this.type = type;
        container = source;
    }

    public Container getContainer() {
        return container;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContainerEvent.class.getSimpleName() + "[", "]")
                .add("container=" + container)
                .add("type='" + type + "'")
                .add("data=" + data)
                .toString();
    }
}

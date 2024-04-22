package com.attackonarchitect.http.session;

import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.utils.AssertUtil;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 会话实现类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/22
 * @since 1.8
 **/
public class Session implements MTSession {
    private final long createTime = System.currentTimeMillis();

    private final String sessionId;

    private transient volatile boolean valid = true;

    private final Map<String, Object> attributes = new HashMap<>();

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public long getCreateTime() {
        this.checkValid();
        return this.createTime;
    }

    @Override
    public String getId() {
        this.checkValid();
        return this.sessionId;
    }

    @Override
    public long getLastAccessTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        this.checkValid();
        return this.attributes.remove(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        this.checkValid();
        return Collections.enumeration(this.attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.checkValid();
        this.attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.checkValid();
        this.attributes.remove(name);
    }

    @Override
    public void invalidate() {
        this.valid = false;
    }


    private void checkValid() {
        AssertUtil.state(this.valid, "会话已经失效");
    }
}

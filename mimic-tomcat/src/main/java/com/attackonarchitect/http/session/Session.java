package com.attackonarchitect.http.session;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ContainerBase;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.listener.session.SessionEvent;
import com.attackonarchitect.servlet.ServletException;
import com.attackonarchitect.utils.AssertUtil;

import java.io.IOException;
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
public class Session extends ContainerBase implements MTSession {
    private final long createTime = System.currentTimeMillis();

    private final String sessionId;

    private transient volatile boolean valid = true;

    private final Map<String, Object> attributes = new HashMap<>();

    private long accessTime = System.currentTimeMillis();

    public Session(Container parent, String sessionId) {
        this.sessionId = sessionId;
        parent.addChild(this);
        this.fireSessionEvent(Session.SESSION_CREATED_EVENT, sessionId);
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
        this.checkValid();
        return this.accessTime;
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

    @Override
    public void access() {

    }

    @Override
    public void expire() {

    }

    @Override
    public void recycle() {

    }


    private void checkValid() {
        AssertUtil.state(this.valid, "会话已经失效");
    }

    @Override
    public String getInfo() {
        return "Mimic Session/version 1.0";
    }

    @Override
    public String getName() {
        return this.getId();
    }

    @Override
    public void invoke(HttpMTRequest request, HttpMTResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    public void fireSessionEvent(final String type, final Object data) {
        this.getNotifiler().notifyListeners(new SessionEvent(this, type, data));
    }
}

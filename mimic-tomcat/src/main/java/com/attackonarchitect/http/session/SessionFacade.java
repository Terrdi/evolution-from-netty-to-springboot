package com.attackonarchitect.http.session;

import com.attackonarchitect.context.ServletContext;

import java.util.Enumeration;
import java.util.Objects;

/**
 * 会话的门面模式
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/22
 * @since 1.8
 **/
public class SessionFacade implements MTSession {
    private final MTSession session;

    public SessionFacade(MTSession session) {
        this.session = Objects.requireNonNull(session, "无法使用已失效的会话");
    }

    @Override
    public long getCreateTime() {
        return this.session.getCreateTime();
    }

    @Override
    public String getId() {
        return this.session.getId();
    }

    @Override
    public long getLastAccessTime() {
        return this.session.getLastAccessTime();
    }

    @Override
    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }

    @Override
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return this.session.getAttributeNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.session.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.session.removeAttribute(name);
    }

    @Override
    public void invalidate() {
        this.session.invalidate();
    }

    @Override
    public void access() {
        this.session.access();
    }

    @Override
    public void expire() {
        this.session.expire();
    }

    @Override
    public void recycle() {
        this.session.recycle();
    }
}

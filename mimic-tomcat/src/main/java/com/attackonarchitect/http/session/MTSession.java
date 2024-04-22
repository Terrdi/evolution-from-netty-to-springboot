package com.attackonarchitect.http.session;

import com.attackonarchitect.context.ServletContext;

import java.util.Enumeration;

/**
 * 会话接口的定义
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/22
 * @since 1.8
 **/
public interface MTSession {
    /**
     * 获取创建时间
     */
    long getCreateTime();

    /**
     * 获取会话编号
     */
    String getId();

    /**
     * 获取上一次访问时间
     */
    long getLastAccessTime();

    /**
     * 获取Servlet上下文
     */
    ServletContext getServletContext();

    /**
     * 获取会话中指定的属性
     * @param name 属性名
     */
    Object getAttribute(String name);

    /**
     * 获取会话中指定的值
     * @param name 指定名称
     */
    default Object getValue(String name) {
        return this.getAttribute(name);
    }

    /**
     * 获取当前所有的属性名
     */
    Enumeration<String> getAttributeNames();

    /**
     * 设置属性的值
     * @param name  属性名
     * @param value 属性值
     */
    void setAttribute(String name, Object value);

    /**
     * 放入属性值和属性
     * @param name  属性名
     * @param value 属性值
     */
    default void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    /**
     * 移除指定属性
     * @param name 属性名
     */
    void removeAttribute(String name);

    /**
     * 移除指定的值
     * @param name 值的名称
     */
    default void removeValue(String name) {
        this.removeAttribute(name);
    }

    /**
     * 将该会话失效
     */
    void invalidate();
}

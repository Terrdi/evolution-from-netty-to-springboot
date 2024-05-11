package com.attackonarchitect.core;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ServletContext;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public interface Context extends Container {
    String RELOAD_EVENT = "reload";

    String getDisplayName();

    void setDisplayName(final String displayName);

    String getDocBase();

    void setDocBase(String docBase);

    String getPath();

    void setPath(String path);

    ServletContext getServletContext();

    int getSessionTimeout();

    void setSessionTimeout(int timeout);

    String getWrapperClass();

    void setWrapperClass(String wrapperClass);

    Wrapper createWrapper();

    String findServletMapping(String pattern);

    String[] findServletMappings();

    void reload();
}

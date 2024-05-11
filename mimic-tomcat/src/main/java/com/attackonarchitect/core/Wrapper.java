package com.attackonarchitect.core;

import com.attackonarchitect.servlet.Servlet;
import com.attackonarchitect.servlet.ServletException;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public interface Wrapper {
    int getLoadOnStartup();

    void setLoadOnStartup(int loadOnStartup);

    String getServletClass();

    void setServletClass(String servletClass);

    void addInitParameter(String name, String value);

    Servlet allocate() throws ServletException;

    String findInitParameter(String name);

    String[] findInitParameters();

    void load() throws ServletException;

    void removeInitParameter(String name);
}

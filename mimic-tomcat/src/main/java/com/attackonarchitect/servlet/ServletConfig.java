package com.attackonarchitect.servlet;

import com.attackonarchitect.context.ServletContext;

import java.util.Iterator;

/**
 * Servlet配置类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/24
 * @since 1.8
 **/
public interface ServletConfig {
    /**
     * servlet名称
     * @return
     */
    String getServletName();

    ServletContext getServletContext();

    Iterator<String> getInitParameterNames();

    String getInitParameter(final String name);
}

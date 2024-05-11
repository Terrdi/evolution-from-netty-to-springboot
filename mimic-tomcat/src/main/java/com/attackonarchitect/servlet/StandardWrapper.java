package com.attackonarchitect.servlet;

import com.attackonarchitect.context.ContainerBase;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.core.Wrapper;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * 标准的Servlet包装容器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public class StandardWrapper extends ContainerBase implements Wrapper {
    private final ServletInformation servlet;

    public StandardWrapper(ServletInformation servlet, ServletContext parent) {
        this.servlet = Objects.requireNonNull(servlet, "无法使用空Servlet对象");
        this.parent = parent;
        this.servlet.setServletContext(parent);
    }

    @Override
    public int getLoadOnStartup() {
        return this.servlet.getLoadOnStartup();
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        this.servlet.setLoadOnStartup(loadOnStartup);
    }

    @Override
    public String getServletClass() {
        return this.servlet.getClazzName();
    }

    @Override
    public void setServletClass(String servletClass) {
        this.servlet.setClazzName(servletClass);
    }

    @Override
    public void addInitParameter(String name, String value) {

    }

    @Override
    public Servlet allocate() throws ServletException {
        return this.servlet;
    }

    @Override
    public String findInitParameter(String name) {
        return null;
    }

    @Override
    public String[] findInitParameters() {
        return new String[0];
    }

    @Override
    public void load() throws ServletException {

    }

    @Override
    public void removeInitParameter(String name) {

    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public void invoke(HttpMTRequest request, HttpMTResponse response) throws IOException, ServletException {
        this.servlet.invoke(request, response);
    }

    public ServletInformation getServlet() {
        return this.servlet;
    }
}

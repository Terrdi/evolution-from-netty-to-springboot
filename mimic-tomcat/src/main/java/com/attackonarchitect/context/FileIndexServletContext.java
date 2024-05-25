package com.attackonarchitect.context;

import com.attackonarchitect.core.Wrapper;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.servlet.DefaultMimicServlet;
import com.attackonarchitect.servlet.MimicServlet;
import com.attackonarchitect.servlet.ServletException;

import java.io.IOException;
import java.util.Optional;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/24
 * @since 1.8
 **/
public class FileIndexServletContext extends ContainerBase implements ServletContext {
    public static final String DEFAULT_SERVLET_CONTEXT = "_default_context";


    @Override
    public <T> void setAttribute(String name, T obj) {

    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Notifier getNotifiler() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public String getDocBase() {
        return "";
    }

    @Override
    public void setDocBase(String docBase) {

    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public ServletContext getServletContext() {
        return this;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {

    }

    @Override
    public String getWrapperClass() {
        return "";
    }

    @Override
    public void setWrapperClass(String wrapperClass) {

    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    @Override
    public String findServletMapping(String pattern) {
        return "";
    }

    @Override
    public String[] findServletMappings() {
        return new String[0];
    }

    @Override
    public void reload() {

    }

    @Override
    public String getInfo() {
        return "FileIndexServletContext/ version 0.1";
    }

    public FileIndexServletContext() {
        servlet.setServletContext(this);
    }

    @Override
    public String getName() {
        return Optional.ofNullable(super.getName()).orElse(DEFAULT_SERVLET_CONTEXT);
    }

    private final MimicServlet servlet = new DefaultMimicServlet();

    @Override
    public void invoke(HttpMTRequest request, HttpMTResponse response) throws IOException, ServletException {
        request.setServletContext(this);
        this.servlet.service(request, response);
    }
}

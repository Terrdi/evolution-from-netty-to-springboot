package com.attackonarchitect.servlet;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ContainerBase;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.http.MTRequest;
import com.attackonarchitect.http.MTResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description:
 *
 *  抽象servlet的信息，包括类名，urlPattern，loadOnStartup，initParams
 *
 */
public class ServletInformation extends ContainerBase implements Servlet {
    private String clazzName;
    private String[] urlPattern;
    private int loadOnStartup;
    private Map<String, String> initParams;

    private ClassLoader classLoader;

    private Servlet instance;

    /**
     * 设置{@linkplain ServletContext Servlet上下文}的方法
     */
    private Method servletContextSetter;

    /**
     * {@linkplain ServletContext Servlet上下文}字段表述
     */
    private Field servletContextField;

    public ServletInformation(){
    }

    public ServletInformation(String clazzName, String[] urlPattern, int loadOnStartup, Map<String, String> initParams) {
        this.clazzName = clazzName;
        this.urlPattern = urlPattern;
        this.loadOnStartup = loadOnStartup;
        this.initParams = initParams;
    }

    public ServletInformation(Servlet instance) {
        this.instance = instance;
        this.clazzName = instance.getClass().getName();
        this.loadOnStartup = 0;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public void setUrlPattern(String[] urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public void setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
    }

    public String getClazzName() {
        return clazzName;
    }

    public String[] getUrlPattern() {
        return urlPattern;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public ClassLoader getClassLoader() {
        return Optional.ofNullable(this.classLoader).orElseGet(Thread.currentThread()::getContextClassLoader);
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setServletContext(ServletContext servletContext) {
        if (Objects.isNull(this.instance)) {
            this.loadServlet();
        }

        try {
            if (Objects.nonNull(this.servletContextSetter)) {
                this.servletContextSetter.invoke(this.instance, servletContext);
                return;
            }

            //Field 直接 set，和 通过 setter 方法的区别在于 setter可以有逻辑处理
            if (Objects.nonNull(servletContextField)) {
                servletContextField.set(instance, servletContext);
                return;
            }

            Class<?> traveler = this.instance.getClass();
            while (Objects.nonNull(traveler) && Objects.isNull(this.servletContextSetter) &&
                    Objects.isNull(this.servletContextField)) {
                try {
                    this.servletContextSetter = traveler.getDeclaredMethod("setServletContext", ServletContext.class);
                    continue;
                } catch (NoSuchMethodException ignore) {}
                try {
                    servletContextField = traveler.getDeclaredField("servletContext");
                    servletContextField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    traveler= traveler.getSuperclass();
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Servlet loadServlet() {
        if (Objects.isNull(this.instance)) {
            String clazzName = this.getClazzName();
            try {
                Class<?> clazz = this.getClassLoader().loadClass(clazzName);
                this.instance = (Servlet) clazz.newInstance();

                // 对于 注解 @WebInitParams 传过来的参数处理
                Map<String, String> initParams = this.getInitParams();
                if(Objects.nonNull(initParams) && !initParams.isEmpty()){
                    Class<?> finalClazz = clazz;
                    Servlet finalInstance = instance;
                    initParams.forEach((key, value) -> {
                        try {
                            String setterName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
                            Method method = finalClazz.getMethod(setterName,value.getClass());
                            method.invoke(finalInstance,value);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                this.instance.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.instance;
    }

    @Override
    public void service(MTRequest req, MTResponse response) throws UnsupportedEncodingException {
        if (Objects.isNull(this.instance)) {
            this.loadServlet();
        }

        this.instance.service(req, response);
    }

    @Override
    public Container getParent() {
        return Optional.ofNullable(super.getParent())
                .orElseGet(this::getServletContext);
    }

    @Override
    public void setParent(Container container) {
        super.setParent(container);
        if (container instanceof ServletContext) {
            this.setServletContext((ServletContext) container);
        }
    }

    @Override
    public ServletContext getServletContext() {
        if (Objects.isNull(this.instance)) {
            this.loadServlet();
        }

        return this.instance.getServletContext();
    }

    @Override
    public void init() {
        this.instance.init();
    }

    @Override
    public String getInfo() {
        return "Minit Servlet Wrapper, version 0.1";
    }

    @Override
    public void invoke(HttpMTRequest request, HttpMTResponse response) throws IOException, ServletException {
        this.service(request, response);
    }

    @Override
    public void addChild(Container container) {}

    @Override
    public Container findChild(String name) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override
    public void removeChild(Container child) {}
}

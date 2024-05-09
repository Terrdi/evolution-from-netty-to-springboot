package com.attackonarchitect.servlet;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.context.ServletContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @description:
 */
public class ServletContainer implements ServletManager {
    private ServletContainer() {
    }

    private ComponentScanner provider;

    private ServletContext servletContext;


    private static class ServletManagerImplHolder {
        static ServletContainer instance = new ServletContainer();
    }

    static ServletManager getInstance(ComponentScanner provider, ServletContext servletContext) {
        ServletContainer impl = ServletManagerImplHolder.instance;
//        impl.setScanPackages(scanPackages);
        impl.setProvider(provider);
        impl.setServletContext(servletContext);
        impl.preInit();
        return impl;
    }

    private void preInit() {
        this.getServletMapping().put("default", new ServletInformation(new DefaultMimicServlet()));
        this.getServletMapping().put("error", new ServletInformation(new ErrorMimicServlet()));

        //新增 loadOnStartup 初始化功能
        Collection<ServletInformation> values = provider.getServletInformationMap().values();

        // 依据loadOnStartup顺序进行初始化
        values.stream().filter(info -> info.getLoadOnStartup() > 0)
                .sorted(Comparator.comparingInt(ServletInformation::getLoadOnStartup))
                .forEachOrdered(ServletInformation::loadServlet);
    }

    //////////////////////


    //////

    /**
     * clazzName - Servlet
     */
    private Map<String, ServletInformation> servletMapping;

    private Map<String, ServletInformation> getServletMapping() {
        if (servletMapping == null) {
            servletMapping = new HashMap<>();
        }
        return servletMapping;
    }

    @Override
    public Servlet getSpecifedServlet(String uri) {
        if (servletMapping.containsKey(uri)) {
            return servletMapping.get(uri);
        }
        Map<String, ServletInformation> servletInformationMap = provider.getServletInformationMap();
        ServletInformation servletInformation = servletInformationMap.get(uri);
//        Map<String, String> webServletComponents = provider.getWebServletComponents();
//        String clazzName = webServletComponents.get(uri);
        Servlet ret;
        ret = servletInformation.loadServlet();
        servletMapping.put(uri, servletInformation);
        return ret;
    }


    /**
     * 实例化Servlet
     * @param servletInformation
     * @return
     */
    @Deprecated
    private Servlet instantiate(ServletInformation servletInformation) {
        return servletInformation.loadServlet();
    }


    @Override
    public Map<String, Servlet> getAllServletMapping(boolean init) {

        //TODO
        return null;
    }


    @Override
    public Set<String> getAllRequestUri() {
        return getProvider().getServletInformationMap().keySet();
    }


    /////////getter ,setter
    public ComponentScanner getProvider() {
        return provider;
    }

    public void setProvider(ComponentScanner provider) {
        this.provider = provider;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

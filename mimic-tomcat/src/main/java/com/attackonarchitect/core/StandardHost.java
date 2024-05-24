package com.attackonarchitect.core;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ContainerBase;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.matcher.MatcherSet;
import com.attackonarchitect.servlet.ServletException;
import com.attackonarchitect.utils.AssertUtil;
import com.attackonarchitect.utils.CommonClassLoader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

/**
 * 主机, 最顶层的容器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/16
 * @since 1.8
 **/
public class StandardHost extends ContainerBase {
    /**
     * host中使用一个map存储了所管理的context, 一个context代表了一个独立的web应用
     */
    private final MatcherSet hostTree = new MatcherSet();

    public StandardHost() {
        // 设置公共类路径加载器
        this.home = System.getProperty("minit.home");
        AssertUtil.isNotBlank(home, "找不到主目录!");
        System.out.println("home: " + home);
        File classPath = new File(home);
        File repository = new File(classPath, "lib");
        try {
            CommonClassLoader loader = new CommonClassLoader(repository);
            if (Boolean.parseBoolean(System.getProperty("minit.delegated"))) {
                loader.setDelegate(true);
            }
            this.setLoader(loader);
        } catch (MalformedURLException e) {
            throw new RuntimeException("设置类路径失败: " + e.getMessage());
        }

        log("Host created.");
    }

    @Override
    public String getInfo() {
        return "Minit host, version 0.1";
    }

    @Override
    public void invoke(HttpMTRequest request, HttpMTResponse response) throws IOException, ServletException {
        ServletContext servletContext = request.getServletContext();
        if (Objects.isNull(servletContext)) {
            servletContext = (ServletContext) this.findChild(request.uri());
        }
        servletContext.invoke(request, response);
    }

    @Override
    public Container findChild(String name) {
        String uri = name;

        WebappClassLoader classLoader = (WebappClassLoader) hostTree.maxStrictMatchValue(uri, '/');
        if (Objects.isNull(classLoader)) {
            System.err.println("找不到 " + uri + "对应的context");
            return super.findChild(name);
        } else {
            return classLoader.getContainer();
        }
    }

    public void start(List<WebappClassLoader> webappClassLoaderList) {
        for (WebappClassLoader classLoader : webappClassLoaderList) {
            hostTree.addCharSequence(classLoader.getPath(), classLoader);
            classLoader.start(this);
        }
    }

    private final String home;

    public String getHome() {
        return this.home;
    }
}

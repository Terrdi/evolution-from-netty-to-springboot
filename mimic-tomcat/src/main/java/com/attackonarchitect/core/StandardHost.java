package com.attackonarchitect.core;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ContainerBase;
import com.attackonarchitect.context.FileIndexServletContext;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.logger.Logger;
import com.attackonarchitect.matcher.MatcherSet;
import com.attackonarchitect.servlet.ServletException;
import com.attackonarchitect.utils.AssertUtil;
import com.attackonarchitect.utils.CommonClassLoader;
import com.attackonarchitect.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        // 如果是空上下文, 制造兜底上下文
        if (Objects.isNull(servletContext)) {
            servletContext = (ServletContext) this.findChild(FileIndexServletContext.DEFAULT_SERVLET_CONTEXT);
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
        this.initLibClassLoader();

        this.addChild(new FileIndexServletContext());

        for (WebappClassLoader classLoader : webappClassLoaderList) {
            hostTree.addCharSequence(classLoader.getPath(), classLoader);
            classLoader.start(this);
        }
    }

    public void start() {
        // 解析出指定目录下的所有文件
        File[] directory = new File(this.home, "webapps").listFiles();
        AssertUtil.notNull(directory, "找不到可启动的应用");
        List<WebappClassLoader> webappClassLoaderList = new ArrayList<>(directory.length);

        for (File d : directory) {
            if (!d.canRead()) {
                this.getLogger().log(d.getName() + " 无权限, 无法解析为网页应用.", Logger.ERROR);
                continue;
            }

            // 尝试解析为WebappClassLoader
            ComponentScanner scanner = WebappClassLoader.resolve(d);
            // 设置其它属性
            if (Objects.nonNull(scanner)) {
                WebappClassLoader loader = new WebappClassLoader();
                final String path = d.getName().equals("ROOT") ? "/" : "/" + StringUtil.resolveSimpleFileName(d.getName());
                final String docbase = d.getPath();

                loader.setPath(path);
                loader.setDocbase(docbase);
                loader.setComponentScanner(scanner);
                webappClassLoaderList.add(loader);

                System.out.println("解析到 " + docbase + " 的扫包方式为: " + scanner.getClass().getSimpleName());
            } else {
                System.err.println(d.getName() + "找不到扫包方式.");
            }
        }

        this.start(webappClassLoaderList);
    }

    private void initLibClassLoader() {
        // 设置公共类路径加载器
        String home = this.getHome();
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
    }

    private String home;

    public String getHome() {
        return Optional.ofNullable(this.home).orElse(System.getProperty("minit.home"));
    }

    public void setHome(final String home) {
        this.home = home;
    }
}

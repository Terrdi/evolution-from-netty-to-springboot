package com.attackonarchitect.core;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.SpiComponentScanner;
import com.attackonarchitect.WebComponentScanner;
import com.attackonarchitect.XmlComponentScanner;
import com.attackonarchitect.context.*;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.listener.NotifierImpl;
import com.attackonarchitect.logger.FileLogger;
import com.attackonarchitect.utils.JarClassLoader;
import com.attackonarchitect.utils.StringUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/15
 * @since 1.8
 **/
public class WebappClassLoader {
//    private ClassLoader classLoader;
    private ComponentScanner scanner;

    private String path;

    private String docbase;

    private Container container;

    private boolean delegated;

    public ComponentScanner getComponentScanner() {
        return this.scanner;
    }

    public void setComponentScanner(ComponentScanner scanner) {
        this.scanner = scanner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocbase() {
        return docbase;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String getInfo() {
        return "a simple loader";
    }

    public boolean isDelegated() {
        return delegated;
    }

    public void setDelegated(boolean delegated) {
        this.delegated = delegated;
    }

    public void addRepository(String repository) {}

    public String[] findRepositories() {
        return new String[0];
    }

    public synchronized void start(Container parent) {
//        try {
//            URL[] urls = new URL[1];
//            URLStreamHandler streamHandler = null;
//            File classPath = new File(System.getProperty("minit.base"));
//            String repository = (new URL("file", null, classPath.getCanonicalPath() +
//                    File.separator)).toString();
//            if (StringUtil.isNotBlank(docbase)) {
//                repository = repository + docbase + File.separator;
//            }
//            urls[0] = new URL(null, repository, streamHandler);
//            System.out.println("Webapp classloader Repository: " + repository);
////            classLoader = new URLClassLoader(urls);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        NotifierImpl notifier = new NotifierImpl(Objects.requireNonNull(this.getComponentScanner(), "没有找到合适的组件扫描器").getWebListenerComponents());
        this.container = ServletContextFactory.getInstance(this.getComponentScanner(), notifier, new FileLogger());
        ((ServletContext) this.container).setPath(this.getPath());
        String docBase = this.getDocbase();
        if (StringUtil.isBlank(docBase) && parent instanceof StandardHost) {
            docBase = ((StandardHost) parent).getHome() + File.separator + this.container.getName();
        }
        ((ServletContext) this.container).setDocBase(docBase);
        File docbase = new File(docBase);
        File repository = docbase.isFile() ? docbase : new File(docbase, "WEB-INF" + File.separator + "classes");
        try {
            com.attackonarchitect.utils.WebappClassLoader loader = new com.attackonarchitect.utils.WebappClassLoader
                    (repository, parent.getLoader());
            loader.setDelegate(this.delegated);
            this.container.setLoader(loader);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        parent.addChild(this.container);

        notifier.init(this.getComponentScanner().getWebListenerComponents(), this.container);
        ((ApplicationContext) this.container).init();
    }

    public void stop() {}

    public static ComponentScanner resolve(File file) {
        ComponentScanner ret = null;
        if (file.isFile()) {
            JarClassLoader classLoader = null;
            // 以jar或者war方式放在该位置
            try {
                classLoader = new JarClassLoader(file, null);
                // 尝试解析出xml
                URL url = classLoader.getResource("web.xml");
                if (Objects.isNull(url)) {
                    url = classLoader.getResource("WEB-INF/web.xml");
                }
                if (Objects.nonNull(url)) {
                    ret = new XmlComponentScanner(file.getPath());
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("解析jar包: " + file.getPath() + " 失败!");
            }

            // 尝试注解启动
            if (Objects.isNull(ret)) {
                String mainClass = classLoader.getMainClass();
                try {
                    if (StringUtil.isNotBlank(mainClass)) {
                        ret = new WebComponentScanner(classLoader.loadClass(mainClass));
                    } else {
                        ret = new SpiComponentScanner(classLoader);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("解析jar包主类: " + file.getPath() + " 失败!");
                }
            }
        } else {
            // 是否存在web.xml文件
            File webXml = new File(file, "web.xml");
            if (!webXml.exists() || !webXml.isFile()) {
                webXml = new File(file, "WEB-INF" + File.separator + "web.xml");
            }
            if (webXml.exists() && webXml.isFile()) {
                ret = new XmlComponentScanner(webXml.getPath(), file.getName());
            }
        }

        return ret;
    }
}

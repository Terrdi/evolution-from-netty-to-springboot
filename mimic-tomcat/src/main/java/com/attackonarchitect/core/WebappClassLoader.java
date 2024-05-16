package com.attackonarchitect.core;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.context.ServletContextFactory;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.listener.NotifierImpl;
import com.attackonarchitect.logger.FileLogger;

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
        Notifier notifier = new NotifierImpl(Objects.requireNonNull(this.getComponentScanner(), "没有找到合适的组件扫描器").getWebListenerComponents());
        this.container = ServletContextFactory.getInstance(this.getComponentScanner(), notifier, new FileLogger());
        ((ServletContext) this.container).setPath(this.getPath());
        ((ServletContext) this.container).setDocBase(this.getDocbase());
        parent.addChild(this.container);
    }

    public void stop() {}
}

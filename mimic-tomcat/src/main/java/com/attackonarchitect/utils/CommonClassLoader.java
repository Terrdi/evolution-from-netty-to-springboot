package com.attackonarchitect.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Optional;

/**
 * 公共类加载器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/20
 * @since 1.8
 **/
public class CommonClassLoader extends URLClassLoader {
    private final ClassLoader system = getSystemClassLoader();

    protected boolean delegate = false;

    public CommonClassLoader(URL[] urls) {
        super(urls);
    }

    public CommonClassLoader(File file) throws MalformedURLException {
        this(resolveUrls(file));
    }

    public CommonClassLoader(File file, ClassLoader parent) throws MalformedURLException {
        this(resolveUrls(file), parent);
    }

    public CommonClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public boolean isDelegate() {
        return delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> ret = null;
        // 首先尝试使用系统类加载器加子啊类, 防止Web应用程序发生覆盖
        try {
            ret = system.loadClass(name);
        } catch (ClassNotFoundException ignore) {
        }

        boolean delegateLoad = this.isDelegate();
        if (Objects.isNull(ret) && delegateLoad) {
            // 系统加载器无法加载该类, 优先代理到父类
            ret = this.delegateLoadClass(name);
        }
        if (Objects.isNull(ret)) {
            // 本地库搜索加载
            try {
                ret = this.findClass(name);
            } catch (ClassNotFoundException ignore) {
            }
        }

        if (Objects.isNull(ret) && !delegateLoad) {
            // 没有优先代理
            // 委托给父类
            ret = this.delegateLoadClass(name);
        }


        if (Objects.isNull(ret)) {
            throw new ClassNotFoundException(name);
        } else {
            if (resolve) {
                this.resolveClass(ret);
            }
            return ret;
        }
    }

    private Class<?> delegateLoadClass(String name) throws ClassNotFoundException {
        ClassLoader parent = Optional.ofNullable(this.getParent()).orElse(this.system);
        Class<?> ret = null;
        try {
             ret = parent.loadClass(name);
        } catch (ClassNotFoundException ignore) {
        }
        return ret;
    }

    private static URL[] resolveUrls(File file) throws MalformedURLException {
        URL[] ret;
        if (file.isFile()) {
            ret = new URL[1];
            ret[0] = file.toURI().toURL();
        } else {
            File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
            if (Objects.nonNull(files) && files.length > 0) {
                ret = new URL[files.length];
                for (int i = 0; i < files.length; i++) {
                    File jarFile = files[i];
                    ret[i] = jarFile.toURI().toURL();
                }
            } else {
                ret = new URL[1];
                ret[0] = file.toURI().toURL();
            }
        }
        return ret;
    }
}

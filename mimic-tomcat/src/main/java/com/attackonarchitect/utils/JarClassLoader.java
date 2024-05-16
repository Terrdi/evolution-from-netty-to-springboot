package com.attackonarchitect.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 使用指定jar作为classloader
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/2
 * @since 1.8
 **/
public class JarClassLoader extends URLClassLoader {
    public JarClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public JarClassLoader(URL[] urls) {
        this(urls, Thread.currentThread().getContextClassLoader());
    }

    public JarClassLoader(File file) throws MalformedURLException {
        this(new URL[]{file.toURI().toURL()});
        this.jarName = FileUtil.getSimpleFileName(file.getName());
    }

    public JarClassLoader(String resource) throws MalformedURLException {
        this(new URL[]{new URL(resource)});
    }

    private String jarName;

    public String getJarName() {
        return jarName;
    }
}

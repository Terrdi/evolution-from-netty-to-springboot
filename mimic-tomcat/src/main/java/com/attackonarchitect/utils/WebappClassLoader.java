package com.attackonarchitect.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 应用类加载器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/22
 * @since 1.8
 **/
public class WebappClassLoader extends CommonClassLoader {
    public WebappClassLoader(URL[] urls) {
        super(urls);
    }

    public WebappClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public WebappClassLoader(File file) throws MalformedURLException {
        super(file);
    }

    public WebappClassLoader(File file, ClassLoader parent) throws MalformedURLException {
        super(file, parent);
    }
}

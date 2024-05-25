package com.attackonarchitect.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Manifest;

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

    public JarClassLoader(File file, ClassLoader parent) throws MalformedURLException {
        this(new URL[]{file.toURI().toURL()}, parent);
        this.jarName = FileUtil.getSimpleFileName(file.getName());
    }

    public JarClassLoader(String resource) throws MalformedURLException {
        this(new URL[]{new URL(resource)});
    }

    private String jarName;

    public String getJarName() {
        return jarName;
    }

    private Map<Object, Object> attrs;

    /**
     * 重置一个类加载器, 使用指定类加载器为父类加载器
     * @param parent
     * @return
     */
    public JarClassLoader clone(final ClassLoader parent) {
        return new JarClassLoader(this.getURLs(), parent);
    }

    public String getAttribute(final String key) {
        if (Objects.isNull(attrs)) {
            this.initAttributes();
        }

        Object val = this.attrs.get(key);
        if (Objects.nonNull(val) && !(val instanceof String)) {
            this.attrs.put(key, val = String.valueOf(val));
        }
        return (String) val;
    }

    public String getMainClass() {
        return this.getAttribute("program-class");
    }

    private synchronized void initAttributes() {
        if (Objects.isNull(attrs)) {
            this.attrs = new HashMap<>();
            URL manifestUrl = this.findResource("META-INF/MANIFEST.MF");
            if (Objects.isNull(manifestUrl)) {
                return;
            }

            try (InputStream is = manifestUrl.openStream()) {
                Manifest manifest = new Manifest(is);
                this.attrs.putAll(manifest.getMainAttributes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

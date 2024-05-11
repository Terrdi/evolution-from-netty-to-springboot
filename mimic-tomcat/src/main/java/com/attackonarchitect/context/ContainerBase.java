package com.attackonarchitect.context;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器基本实现
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/10
 * @since 1.8
 **/
public abstract class ContainerBase implements Container {
    /**
     * 子容器
     */
    protected final Map<String, Container> children = new ConcurrentHashMap<>();

    /**
     * 当前容器的类加载器
     */
    protected ClassLoader loader = null;

    /**
     * 容器名称
     */
    protected String name;

    /**
     * 父容器
     */
    protected Container parent;

    @Override
    public ClassLoader getLoader() {
        return Optional.ofNullable(this.loader)
                .orElseGet(() -> Optional.ofNullable(this.parent)
                        .map(Container::getLoader).orElse(null));
    }

    @Override
    public synchronized void setLoader(ClassLoader loader) {
        final ClassLoader oldLoader = this.loader;
        if (oldLoader != loader) {
            this.loader = loader;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Container getParent() {
        return this.parent;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
    }

    @Override
    public void addChild(Container container) {
        this.addChildInternal(container);
    }

    @Override
    public Container findChild(String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        synchronized (children) {
            return this.children.get(name);
        }
    }

    @Override
    public Container[] findChildren() {
        synchronized (children) {
            return this.children.values().toArray(new Container[0]);
        }
    }

    @Override
    public void removeChild(Container child) {
        Container parent = child.getParent();
        if (parent == this) {
            child.setParent(null);
        }

        synchronized (children) {
            this.children.remove(child.getName(), child);
        }
    }

    private void addChildInternal(Container child) {
        synchronized (children) {
            Container oldChild = children.get(child.getName());
            if (Objects.nonNull(oldChild)) {
                throw new IllegalArgumentException(String.format("addChild: Child name '%s" +
                        "' is not unique", child.getName()));
            }

            child.setParent(this);
            children.put(child.getName(), child);
        }
    }
}

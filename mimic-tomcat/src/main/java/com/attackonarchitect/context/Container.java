package com.attackonarchitect.context;

import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.logger.Logger;
import com.attackonarchitect.servlet.ServletException;

import java.io.IOException;

/**
 * 容器接口
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/10
 * @since 1.8
 **/
public interface Container {
    String ADD_CHILD_EVENT = "addChild";

    String REMOVE_CHILD_EVENT = "removeChild";

    /**
     * 获取简要描述信息
     */
    String getInfo();

    /**
     * 获取类加载器
     */
    ClassLoader getLoader();

    /**
     * 设置类加载器
     */
    void setLoader(ClassLoader loader);

    /**
     * 获取容器名称
     */
    String getName();

    /**
     * 设置容器名称
     */
    void setName(final String name);

    /**
     * 获取当前父容器
     */
    Container getParent();

    /**
     * 设置当前父容器
     */
    void setParent(Container container);

    /**
     * 添加子容器
     */
    void addChild(Container container);

    /**
     * 依据名称查找子容器
     * @param name  子容器名称
     * @return 查找到的子容器
     */
    Container findChild(final String name);

    /**
     * 获取当前所有子容器
     */
    Container[] findChildren();

    /**
     * 取消当前容器和指定容器的父子关系
     * @param child 子容器
     */
    void removeChild(final Container child);

    /**
     * 调用服务类
     *
     * @param request       请求对象
     * @param response      返回对象
     * @throws IOException  发生IO错误
     * @throws ServletException 调用错误
     */
    void invoke(HttpMTRequest request, HttpMTResponse response)
        throws IOException, ServletException;

    Logger getLogger();

    void setLogger(Logger logger);
}

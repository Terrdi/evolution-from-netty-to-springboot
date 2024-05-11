package com.attackonarchitect.core;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;

/**
 * 连接器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/10
 * @since 1.8
 **/
public interface Connector {
    /**
     * 获取当前连接器的容器
     */
    Container getContainer();

    /**
     * 设置当前容器
     * @param container 容器
     */
    void setContainer(Container container);

    /**
     * 获取信息
     * @return
     */
    String getInfo();

    String getSchema();

    void setSchema(final String schema);

    HttpMTRequest createRequest();

    HttpMTResponse createResponse();

    void initialize();
}

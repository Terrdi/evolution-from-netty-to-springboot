package com.attackonarchitect.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public interface Request {
    Connector getConnector();

    void setConnector(Connector connector);

    Context getContext();

    void setContext(Context context);

    String getInfo();

    /**
     * todo 获取servletrequest对象
     * @return
     */
    Request getRequest();

    Response getResponse();

    void setResponse(Response response);

    Socket getSocket();

    void setSocket(Socket socket);

    InputStream getSteam();

    void setStream(InputStream stream);

    Wrapper getWrapper();

    void setWrapper(Wrapper wrapper);

    InputStream createInputStream() throws IOException;

    void finishRequest() throws IOException;

    void recycle();

    void setContentLength(int length);

    void setContentType(String type);

    void setProtocol(String protocol);

    void setRemoteAddr(String remote);

    void setSchema(String schema);

    void setServerPort(int port);
 }

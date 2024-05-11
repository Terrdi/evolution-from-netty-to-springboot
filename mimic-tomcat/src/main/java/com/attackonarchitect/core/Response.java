package com.attackonarchitect.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public interface Response {
    Connector getConnector();

    void setConnector(Connector connector);

    Context getContext();

    void setContext(Context context);

    String getInfo();

    Request getRequest();

    void setRequest(Request request);

    /**
     * todo 获取servletresponse对象
     * @return
     */
    Response getResponse();

    OutputStream getSteam();

    void setStream(OutputStream stream);

    void setError();

    boolean isError();

    OutputStream createOutputStream() throws IOException;

    void finishResponse() throws IOException;

    void recycle();

    void getContentLength();

    void getContentType();

    PrintWriter getReporter();

    void sendAcknowledgement() throws IOException;

    void resetBuffer();
 }

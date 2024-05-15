package com.attackonarchitect.listener.request;

import com.attackonarchitect.core.Wrapper;
import com.attackonarchitect.filter.chain.Filter;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.listener.Event;
import com.attackonarchitect.servlet.Servlet;

import java.util.EventObject;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/14
 * @since 1.8
 **/
public class InstanceEvent extends EventObject implements Event {
    public static final String BEFORE_INIT_EVENT = "beforeInit";
    public static final String AFTER_INIT_EVENT = "afterInit";
    public static final String BEFORE_SERVICE_EVENT = "beforeService";
    public static final String AFTER_SERVICE_EVENT = "afterService";
    public static final String BEFORE_DESTROY_EVENT = "beforeDestroy";
    public static final String AFTER_DESTROY_EVENT = "afterDestroy";
    public static final String BEFORE_DISPATCH_EVENT = "beforeDispatch";
    public static final String AFTER_DISPATCH_EVENT = "afterDispatch";
    public static final String BEFORE_FILTER_EVENT = "beforeFilter";
    public static final String AFTER_FILTER_EVENT = "afterFilter";


    private final Wrapper wrapper;
    private final Filter filter;
    private final Servlet servlet;
    private final String type;
    private final HttpMTRequest request;
    private final HttpMTResponse response;
    private final Throwable exception;

    public InstanceEvent(Wrapper wrapper, Filter filter, Servlet servlet, String type, HttpMTRequest request,
                         HttpMTResponse response, Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.servlet = servlet;
        this.type = type;
        this.request = request;
        this.response = response;
        this.exception = exception;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type, Throwable exception) {
        this(wrapper, filter, null, type, null, null, exception);
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type, HttpMTRequest request,
                         HttpMTResponse response, Throwable exception) {
        this(wrapper, filter, null, type, request, response, exception);
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type) {
        this(wrapper, filter, type, null);
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type, HttpMTRequest request,
                         HttpMTResponse response) {
        this(wrapper, filter, type, request, response, null);
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type, Throwable exception) {
        this(wrapper, null, servlet, type, null, null, exception);
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type, HttpMTRequest request,
                         HttpMTResponse response, Throwable exception) {
        this(wrapper, null, servlet, type, request, response, exception);
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type) {
        this(wrapper, servlet, type, null);
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type, HttpMTRequest request,
                         HttpMTResponse response) {
        this(wrapper, servlet, type, request, response, null);
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public Filter getFilter() {
        return filter;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public HttpMTRequest getRequest() {
        return request;
    }

    public String getType() {
        return type;
    }

    public HttpMTResponse getResponse() {
        return response;
    }

    public Throwable getException() {
        return exception;
    }
}

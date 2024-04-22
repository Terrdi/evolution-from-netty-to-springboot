package com.attackonarchitect.http;

import com.attackonarchitect.http.cookie.MTCookie;
import com.attackonarchitect.http.cookie.MTCookieBuilder;
import com.attackonarchitect.http.session.MTSession;
import com.attackonarchitect.http.session.SessionFacade;
import com.attackonarchitect.http.session.SessionFactory;
import com.attackonarchitect.utils.AssertUtil;
import com.attackonarchitect.utils.StringUtil;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.listener.request.ServletRequestAttributeEvent;
import com.attackonarchitect.listener.request.ServletRequestAttributeListener;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @description:
 */
public class HttpMTRequest implements MTRequest{

    @Override
    public HttpMethod getMethod() {
        return null;
    }

    @Override
    public HttpMethod method() {
        return null;
    }

    @Override
    public HttpRequest setMethod(HttpMethod method) {
        return null;
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public String uri() {
        return null;
    }

    @Override
    public HttpRequest setUri(String uri) {
        return null;
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return null;
    }

    @Override
    public HttpVersion protocolVersion() {
        return null;
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion version) {
        return null;
    }

    @Override
    public HttpHeaders headers() {
        return null;
    }

    @Override
    public DecoderResult getDecoderResult() {
        return null;
    }

    @Override
    public DecoderResult decoderResult() {
        return null;
    }

    @Override
    public void setDecoderResult(DecoderResult result) {

    }

    private ServletContext context;

    private Map<String,Object> attributeDepot = new HashMap<>();

    public Object getAttribute(String name){
        return attributeDepot.get(name);
    }


    public void setAttribute(String name, Object obj){
        attributeDepot.put(name,obj);
        ServletRequestAttributeEvent srae = new ServletRequestAttributeEvent(name, obj);
        context.getNotifiler().notifyListeners(ServletRequestAttributeListener.class,srae);
    }

    //////////////////// parameters
    private Map<String,String> parametersDepot ;

    public void setParametersDepot(Map<String, String> parametersDepot) {
        this.parametersDepot = parametersDepot;
    }

    @Override
    public Map<String, String> getParameters() {
        return this.parametersDepot;
    }

    @Override
    public String getParameter(String name) {
        return this.parametersDepot.get(name);
    }

    // region 请求头
    private Map<String, String> headers = new HashMap<>();

    public void parseHttpHeaders(final HttpRequest httpRequest) {
        Iterator<Map.Entry<String, String>> headerIterator = httpRequest.headers().iteratorAsString();
        while (headerIterator.hasNext()) {
            Map.Entry<String, String> header = headerIterator.next();
            headers.put(header.getKey(), header.getValue());
        }

        this.initCookie();
    }

    @Override
    public String getHeader(String name) {
        return this.headers.get(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        String value = this.getHeader(name);
        return Optional.ofNullable(value).map(v -> v.split(";"))
                .map(Stream::of).orElseGet(Stream::empty).collect(Collectors.toList());
    }

    @Override
    public Iterator<String> getHeaderNames() {
        return this.headers.keySet().iterator();
    }
    //endregion

    //region cookie
    private final Map<String, MTCookie> cookieMap = new HashMap<>();

    private void initCookie() {
        List<String> cookieValue = this.getHeaders("Cookie");
        cookieValue.addAll(this.getHeaders("cookie"));

        for (String cookieString : cookieValue) {
            String[] kv = cookieString.split("=", 2);
            AssertUtil.state(kv.length == 2, "不合法的Cookie: " + cookieString);
            this.cookieMap.put(kv[0], MTCookieBuilder.newBuilder().name(kv[0]).value(kv[1]).build());
        }
    }

    @Override
    public String getCookie(String cookie) {
        return Optional.ofNullable(cookieMap.get(cookie)).map(MTCookie::getValue).orElse(null);
    }

    @Override
    public Iterator<String> getCookieNames() {
        return cookieMap.keySet().iterator();
    }
    //endregion

    //region session
    private MTSession session;

    public MTSession getSession(boolean create) {
        if (Objects.isNull(session)) {
            String id = this.getSessionId();
            boolean noId = StringUtil.isBlank(id);
            if (noId && create) {
                session = new SessionFacade(SessionFactory.createSession());
                id = session.getId();
                this.setAttribute(SessionFactory.SESSION_NAME, id);
            } else if (!noId) {
                session = new SessionFacade(SessionFactory.getSession(id));
            }
        }

        return session;
    }

    public MTSession getSession() {
        return this.getSession(false);
    }

    @Override
    public MTSession openSession() {
        return this.getSession(true);
    }

    private String getSessionId() {
        String sessionId = this.getCookie(SessionFactory.SESSION_NAME);
        if (StringUtil.isBlank(sessionId)) {
            sessionId = this.parametersDepot.get(SessionFactory.SESSION_NAME);
        }
        if (StringUtil.isBlank(sessionId)) {
            sessionId = this.getHeader(SessionFactory.SESSION_NAME);
        }

        return sessionId;
    }
    //endregion

    public HttpMTRequest(ServletContext context) {
        this.context = context;
    }



    @Override
    public ServletContext getServletContext() {
        return this.context;
    }
}

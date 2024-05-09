package com.attackonarchitect.http;

import com.attackonarchitect.http.cookie.MTCookie;
import com.attackonarchitect.http.cookie.MTCookieBuilder;
import com.attackonarchitect.http.session.MTSession;
import com.attackonarchitect.http.session.SessionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description:
 */
public class HttpMTResponse implements MTResponse{
    protected final ChannelHandlerContext ctx;

    private final Map<String, String> headers = new HashMap<>();

    private final Map<String, MTCookie> cookieMap = new HashMap<>();

    protected int statusCode = HttpResponseStatus.OK.code();

    private ByteBuf byteBuf;

    private HttpMTRequest request;

    private boolean sendComplete = false;

    public HttpMTResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 代理字段, 优先尝试该返回结果
     */
    protected HttpMTFileResponse delegate;

    private StringBuilder contents = new StringBuilder();

    @Override
    public void write(String content)  {
        if (Objects.isNull(delegate)) {
            contents.append(content).append(System.lineSeparator());
        } else {
            delegate.write(content);
        }
    }

    @Override
    public void write(byte[] bytes, int offset, int len) {
        if (isChunked()) {
            try {
                this.doFlush();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
//                this.ctx.write(new HttpChunkedInput(new ChunkedStream(new ByteArrayInputStream(bytes, offset, len))));
            this.ctx.write(new DefaultHttpContent(Unpooled.wrappedBuffer(bytes, offset, len)));

        } else {
            if (Objects.isNull(this.byteBuf)) {
                this.byteBuf = Unpooled.buffer(1 << 20);
            }

            this.byteBuf.writeBytes(bytes, offset, len);
        }
    }

    private boolean isChunked() {
        String val = this.headers.get(HttpHeaderNames.TRANSFER_ENCODING.toString());
        return Objects.nonNull(val) && val.equalsIgnoreCase(HttpHeaderValues.CHUNKED.toString());
    }


    // 如何把这个方法设置为内部接口？？？
    @Override
    public void flush() throws UnsupportedEncodingException {
        if (Objects.isNull(this.delegate)) {
            this.doFlush();
        } else {
            this.delegate.flush();
        }
    }

    private ByteBuf resolveByteBuf() {
        ByteBuf ret;
        if (Objects.nonNull(contents) && contents.length() > 0) {
            String content = contents.toString();
            if (content.startsWith("<!DOCTYPE html>") || content.startsWith("<html>")) {
                this.addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.TEXT_HTML.toString());
            }
            ret = Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8));
        } else {
            ret = Unpooled.EMPTY_BUFFER;
        }
        return ret;
    }

    private void flushRequestInfo() {
        if (Objects.isNull(this.request)) {
            return;
        }

        // 检查是否存在session
        if (!this.cookieMap.containsKey(SessionFactory.SESSION_NAME)) {
            MTSession session = request.getSession();
            if (Objects.nonNull(session)) {
                this.setCookie(SessionFactory.SESSION_NAME, session.getId());
            }
        }
    }

    private void doFlush() throws UnsupportedEncodingException {
        if (this.sendComplete) {
            if (this.isChunked()) {
                this.ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }

        ByteBuf byteBuf = Optional.ofNullable(this.byteBuf).orElseGet(this::resolveByteBuf);

        HttpResponse response =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(statusCode),
                        byteBuf
                );

        HttpHeaders headers = response.headers();
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            if (headers.contains(header.getKey())) {
                System.err.println("已经设置的响应消息头: " + header.getKey());
            } else {
                headers.set(header.getKey(), header.getValue());
            }
        }
        if (!headers.containsValue(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED, true) &&
            !headers.contains(HttpHeaderNames.CONTENT_LENGTH)) {
            headers.set(HttpHeaderNames.CONTENT_TYPE, Optional.ofNullable(this.headers.remove(HttpHeaderNames.CONTENT_TYPE.toString()))
                .orElse("text/plain;charset=utf-8"));
            headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        } else {
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            // 不是一次全部输出
            response = new DefaultHttpResponse(response.protocolVersion(), response.status(), headers);
        }

        this.flushRequestInfo();

        for (MTCookie cookie : cookieMap.values()) {
            headers.add(HttpHeaderNames.SET_COOKIE, cookie.toString());
        }

        ctx.write(response).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                HttpMTResponse.this.sendComplete = true;
            }
        });
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    @Override
    public void setCookie(final String cookieName, final String cookieValue) {
        MTCookie cookie = MTCookieBuilder.newBuilder()
                .name(cookieName).value(cookieValue)
                .build();
        this.cookieMap.put(cookieName, cookie);
    }

    @Override
    public void setCookie(final String cookieName, final MTCookie cookie) {
        this.cookieMap.put(cookieName, cookie);
    }

    @Override
    public void setCookie(final MTCookie cookie) {
        this.cookieMap.put(Objects.requireNonNull(cookie, "请生成一个Cookie").getName(), cookie);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusCode(HttpResponseStatus status) {
        this.statusCode = Optional.ofNullable(status).orElse(HttpResponseStatus.INTERNAL_SERVER_ERROR).code();
    }

    public void setDelegate(HttpMTFileResponse delegate) {
        this.delegate = delegate;
    }

    public void setRequest(HttpMTRequest request) {
        if (Objects.isNull(this.delegate)) {
            this.request = request;
        } else {
            this.delegate.setRequest(request);
        }
    }

    /**
     * 清空当前缓存区
     */
    public void clear() {
        if (Objects.isNull(this.delegate)) {
            if (Objects.nonNull(this.contents)) {
                this.contents = new StringBuilder();
            }
            if (Objects.nonNull(this.byteBuf)) {
                this.byteBuf = this.byteBuf.clear();
            }
        } else {
            this.delegate.clear();
        }
    }
}

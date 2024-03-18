package com.attackonarchitect.http;

import com.attackonarchitect.http.cookie.MTCookie;
import com.attackonarchitect.http.cookie.MTCookieBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

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
        if (Objects.isNull(this.byteBuf)) {
            this.byteBuf = Unpooled.buffer(1 << 20);
        }

        this.byteBuf.writeBytes(bytes, offset, len);
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
            ret = Unpooled.copiedBuffer(contents.toString().getBytes(StandardCharsets.UTF_8));
        } else {
            ret = Unpooled.EMPTY_BUFFER;
        }
        return ret;
    }

    private void doFlush() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Optional.ofNullable(this.byteBuf).orElseGet(this::resolveByteBuf);

        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(statusCode),
                        byteBuf
                );

        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, Optional.ofNullable(this.headers.remove(HttpHeaderNames.CONTENT_TYPE.toString())).orElse("text/plain;charset=utf-8"));
        headers.set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());


        for (MTCookie cookie : cookieMap.values()) {
            headers.add(HttpHeaderNames.SET_COOKIE, cookie.toString());
        }

        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            if (headers.contains(header.getKey())) {
                System.err.println("已经设置的响应消息头: " + header.getKey());
            } else {
                headers.set(header.getKey(), header.getValue());
            }
        }

        ctx.writeAndFlush(response);
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
}

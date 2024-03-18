package com.attackonarchitect.http;

import com.attackonarchitect.http.cookie.MTCookie;

import java.io.UnsupportedEncodingException;

/**
 * @description:
 */

public interface MTResponse {
    void write(String content);

    void write(byte[] bytes, int off, int len);

    default void write(byte[] bytes) {
        this.write(bytes, 0, bytes.length);
    }

    /**
     * Developers shouldn't call this method
     * @throws UnsupportedEncodingException
     */
    void flush() throws UnsupportedEncodingException;

    void addHeader(final String headerName, final String headerValue);

    void setCookie(final String cookieName, final String cookieValue);

    void setCookie(final String cookieName, final MTCookie cookie);

    void setCookie(final MTCookie cookie);
}

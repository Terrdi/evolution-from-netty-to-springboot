package com.attackonarchitect.filter.chain;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.http.MTRequest;
import com.attackonarchitect.http.MTResponse;
import com.attackonarchitect.servlet.ServletException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 过滤器
 *
 * @description:
 */

public interface Filter {
    boolean doFilter(MTRequest request, MTResponse response, FilterChain filterChain) throws IOException, ServletException;

    default String getInfo() {
        return "Mimic Filter/1.0";
    }

    default Container getContainer() {
        return null;
    }

    default void setContainer(Container container) {}
}

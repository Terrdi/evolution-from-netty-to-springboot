package com.attackonarchitect.handler;

import com.attackonarchitect.servlet.Servlet;
import com.attackonarchitect.servlet.ServletInformation;

/**
 * @description:
 */

public interface RouteStrategy {
    ServletInformation route(String uri);
}

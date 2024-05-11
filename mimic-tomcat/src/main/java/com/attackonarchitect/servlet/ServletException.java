package com.attackonarchitect.servlet;

/**
 * Servlet调用错误
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/10
 * @since 1.8
 **/
public class ServletException extends RuntimeException {
    public ServletException() {
        super();
    }

    public ServletException(String message) {
        super(message);
    }

    public ServletException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServletException(Throwable cause) {
        super(cause);
    }

    protected ServletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

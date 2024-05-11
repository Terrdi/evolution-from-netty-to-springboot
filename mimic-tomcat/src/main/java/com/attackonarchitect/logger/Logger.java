package com.attackonarchitect.logger;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public interface Logger {
    int FATAL = Integer.MIN_VALUE;

    int ERROR = 1;

    int WARNING = 2;

    int INFORMATION = 3;

    int DEBUG = 4;

    int TRACE = 5;

    String getInfo();

    int getVerbosity();

    void setVerbosity(int verbosity);

    void log(String message);

    default void log(Exception exception, String msg) {
        this.log(msg, exception);
    }

    void log(String message, Throwable throwable);

    void log(String message, int verbosity);

    void log(String message, Throwable throwable, int verbosity);
}

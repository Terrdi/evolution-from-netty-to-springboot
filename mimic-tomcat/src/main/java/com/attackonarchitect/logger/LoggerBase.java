package com.attackonarchitect.logger;

import com.attackonarchitect.servlet.ServletException;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * 日志基础类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public abstract class LoggerBase implements Logger {
    protected int debug = 0;

    protected static final String info = LoggerBase.class.getName() + "/1.0";

    protected int verbosity = ERROR;

    public int getDebug() {
        return this.debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public int getVerbosity() {
        return this.verbosity;
    }

    @Override
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    public void setVerbosityLevel(String verbosity) {
        if ("FATAL".equalsIgnoreCase(verbosity)) {
            this.verbosity = FATAL;
        } else if ("ERROR".equalsIgnoreCase(verbosity)) {
            this.verbosity = ERROR;
        } else if ("WARNING".equalsIgnoreCase(verbosity) ||
                "WARN".equalsIgnoreCase(verbosity)) {
            this.verbosity = WARNING;
        } else if ("INFORMATION".equalsIgnoreCase(verbosity) ||
                "INFO".equalsIgnoreCase(verbosity)) {
            this.verbosity = INFORMATION;
        } else if ("DEBUG".equalsIgnoreCase(verbosity)) {
            this.verbosity = DEBUG;
        } else if ("TRACE".equalsIgnoreCase(verbosity)) {
            this.verbosity = TRACE;
        } else {
            throw new IllegalArgumentException("无法识别的日志级别: " + verbosity);
        }
    }

    @Override
    public void log(String message, Throwable throwable) {
        CharArrayWriter buf = new CharArrayWriter();
        PrintWriter writer = new PrintWriter(buf);
        writer.println(message);
        throwable.printStackTrace(writer);

        Throwable rootCase = null;
        if (throwable instanceof ServletException) {
            rootCase = rootCase.getCause();
        }
        if (Objects.nonNull(rootCase)) {
            writer.println("-------- Root Cause ----------");
            rootCase.printStackTrace(writer);
        }
        log(buf.toString());
    }

    @Override
    public void log(String message, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message);
        }
    }

    @Override
    public void log(String message, Throwable throwable, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message, throwable);
        }
    }
}

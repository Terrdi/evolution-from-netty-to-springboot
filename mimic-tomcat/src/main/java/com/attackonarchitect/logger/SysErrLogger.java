package com.attackonarchitect.logger;

/**
 * 系统错误输出
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public class SysErrLogger extends LoggerBase implements Logger {
    protected static final String info = "com.minit.logger.SysErrLogger/0.1";

    public SysErrLogger() {
        super();
        this.setVerbosity(ERROR);
    }

    @Override
    public void log(String message) {
        System.err.println(message);
    }
}

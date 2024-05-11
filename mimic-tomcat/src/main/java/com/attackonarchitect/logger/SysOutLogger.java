package com.attackonarchitect.logger;

/**
 * 系统标准输出
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public class SysOutLogger extends LoggerBase implements Logger {
    protected static final String info = "com.minit.logger.SystemOutLogger/1.0";

    public SysOutLogger() {
        super();
        this.setVerbosity(INFORMATION);
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}

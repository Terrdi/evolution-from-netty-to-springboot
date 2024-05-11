package com.attackonarchitect.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 文件输出日志
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public class FileLogger extends LoggerBase implements Logger {
    private volatile String date = "";

    private String directory = "target/logs";

    protected static final String info = "com.minit.logger.FileLogger/0.1";

    private String prefix = "minit.";

    private boolean started = false;

    private String suffix = ".log";

    private boolean timestamp = true;

    private PrintWriter writer = null;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isTimestamp() {
        return timestamp;
    }

    public void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void log(String message) {
        // 当前时间
        // Construct the timestamp we will use, if requested.
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tsString = ts.toString().substring(0, 19);
        String tsDate = ts.toString().substring(0, 10);

        // 如果日期变化了, 生成一个新的日志文件
        if (!tsDate.equals(this.date)) {
            synchronized (this) {
                if (!tsDate.equals(this.date)) {
                    close();
                    this.date = tsDate;
                    open();
                }
            }
        }

        if (Objects.nonNull(writer)) {
            if (timestamp) {
                writer.print(tsString);
                writer.print(" ");
            }
            writer.println(message);
        }
    }

    private void open() {
        File dir = new File(directory);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("catalina.base",
                    System.getProperty("user.dir")), directory);
        }

        dir.mkdirs();

        // 打开日志文件
        try {
            String pathname = dir.getAbsolutePath() + File.separator +
                    prefix + date + suffix;
            writer = new PrintWriter(new FileWriter(pathname, true), true);
        } catch (IOException e) {
            writer = null;
        }
    }

    private void close() {
        if (Objects.nonNull(writer)) {
            this.writer.flush();
            this.writer.close();
            this.writer = null;
        }
        this.date = "";
    }
}

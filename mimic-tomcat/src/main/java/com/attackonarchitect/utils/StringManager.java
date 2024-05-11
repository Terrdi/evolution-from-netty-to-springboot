package com.attackonarchitect.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字符串输出类, 主要用于日志输出
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/11
 * @since 1.8
 **/
public class StringManager {
    private StringManager(String packageName) {}

    public String getString(String key) {
        return Objects.requireNonNull(key, "key is null");
    }

    /**
     * 使用参数拼接字符串
     * @param key
     * @param args
     * @return
     */
    public String getString(String key, Object... args) {
        String iString = null, value = getString(key);

        try {
            // 消除null对象
            Object[] nonNullArgs = args;
            for (int i = 0; i < args.length; i++) {
                if (Objects.isNull(args[i])) {
                    if (nonNullArgs == args) {
                        nonNullArgs = args.clone();
                    }
                    nonNullArgs[i] = "null";
                }
            }
            //拼串
            iString = MessageFormat.format(value, nonNullArgs);
        } catch (IllegalArgumentException iae) {
            StringBuilder buf = new StringBuilder();
            buf.append(value);
            for (int i = 0; i < args.length; i++) {
                buf.append(" arg[").append(i).append("] = ").append(args[i]);
            }
            iString = buf.toString();
        }

        return iString;
    }

    private static Map<String, StringManager> managers = new HashMap<>();

    /**
     * 每个包都有相应的StringManager
     * @param packageName
     * @return
     */
    public static synchronized StringManager getManager(final String packageName) {
        return managers.computeIfAbsent(packageName, StringManager::new);
    }
}

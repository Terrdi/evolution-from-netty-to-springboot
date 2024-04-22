package com.attackonarchitect.utils;

import java.util.Random;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/22
 * @since 1.8
 **/
public abstract class RandomUtil {
    private static final Random random;

    static {
        random = new Random(System.currentTimeMillis());
    }

    public static void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }
}
